# Plan 001: Turnier-Kapazität und Statistiken (Backend)

## Status

- **Priority**: P2
- **Effort**: M
- **Risk**: MEDIUM — ändert das Verhalten eines bestehenden, produktiv genutzten Endpunkts (`addSpielerToTurnier` kann jetzt 400 statt 204 liefern) und den Create-Request-Vertrag (`maxTeilnehmer` wird Pflicht)
- **Depends on**: none (Voraussetzung *für* das Frontend-Gegenstück `plans/007-turnier-kapazitaet-und-statistiken.md` im Repo `schach-frontend`, nicht umgekehrt)
- **Category**: feature
- **Planned at**: 2026-08-26, `/grill-with-docs`-Session (kein `improve`-Audit-Commit-Hash — dieser Plan entstand aus einer mehrrundigen Interview-Session mit dem Operator, gemeinsam mit dem Frontend-Repo geplant)

Zwei begleitende ADRs sind bereits im Repo: `docs/adr/0001-max-teilnehmer-nullable-pflichtfeld.md` und `docs/adr/0002-kapazitaets-blockierung-beim-hinzufuegen.md`. Begriffsdefinitionen in `CONTEXT.md`.

## Why this matters

Das Frontend will pro Turnier ein Kreisdiagramm für "Anteil belegter Plätze" anzeigen. Der Zähler existiert schon (`teilnehmerAnzahl`, aus `spielerIds.size()` abgeleitet), der Nenner fehlt komplett — kein Kapazitätsfeld in `Turnier`, keinem DTO, keiner Validierung. Zusätzlich sollen zwei weitere Statistiken (Vereinsanzahl, Ø Rating der Teilnehmer) mitgeliefert werden, um im Frontend N+1-Requests in der neuen Kartenübersicht zu vermeiden.

## Work Item 1: Feld `maxTeilnehmer` auf `Turnier`

**Datei:** `model/Turnier.java`

Neues Feld `private Integer maxTeilnehmer;` (boxed `Integer`, nicht `int` — muss `null` können). Getter/Setter ergänzen. Kein `@Column(nullable = false)` — siehe ADR 0001, `ddl-auto=update` kann auf der befüllten Tabelle keine NOT-NULL-Spalte erzwingen. Die neue Spalte wird beim nächsten Deploy automatisch angelegt (nullable, keine Migration nötig).

## Work Item 2: DTOs erweitern

- **`TurnierCreateDTO`** (`dto/request/turnier/TurnierCreateDTO.java`): Feld `Integer maxTeilnehmer` ergänzen: `record TurnierCreateDTO(String name, LocalDate datum, String ort, Integer maxTeilnehmer, List<Integer> spielerIds)`.
- **`TurnierUpdateDTO`**: ebenfalls `Integer maxTeilnehmer` ergänzen (nullable, optional) — auch ohne aktuelles Edit-Formular im Frontend, damit ein künftiges Edit-Feature nicht nochmal am DTO-Vertrag rütteln muss.
- **`TurnierResponseDTO`** (`dto/response/turnier/TurnierResponseDTO.java`): drei neue Felder: `Integer maxTeilnehmer`, `Integer vereinsAnzahl`, `Double durchschnittsRating` (nullable — siehe Work Item 3 für die 0-Teilnehmer-Regel).

## Work Item 3: Neuer `TurnierStatistikService` (statt mehr Last auf `TurnierService`)

**Neue Datei:** `service/TurnierStatistikService.java`

`TurnierService` ist im Deep-Modules-Review vom 2026-08-25 bereits als God-Service dokumentiert (CRUD, Spieler-/MatchUp-Verknüpfung, Vereins-Aggregation, Rating-Berechnung — fünf Verantwortlichkeiten hinter einem Klassennamen). Statt `vereinsAnzahl`/`durchschnittsRating` als sechste Verantwortlichkeit reinzustapeln: eigener Service nach dem Vorbild von `RatingService` ("reine Mathematik, kein Repository-Zugriff").

```java
@Service
public class TurnierStatistikService {

    public record TurnierStatistik(int vereinsAnzahl, Double durchschnittsRating) {}

    public TurnierStatistik berechne(List<Spieler> teilnehmer) {
        int vereinsAnzahl = (int) teilnehmer.stream()
                .filter(s -> s.getVerein() != null)
                .map(s -> s.getVerein().getVereinId())
                .distinct()
                .count();

        List<Double> ratings = teilnehmer.stream()
                .map(Spieler::getRating)
                .filter(r -> r != null) // falls Rating je nullable wird
                .toList();

        Double durchschnitt = ratings.isEmpty() ? null
                : ratings.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        return new TurnierStatistik(vereinsAnzahl, durchschnitt);
    }
}
```

Nimmt bewusst `List<Spieler>` entgegen statt `Turnier` oder einer ID — reine Funktion, kein eigener Repository-Zugriff, genau wie `RatingService.dresden(...)`/`elo(...)` reine Zahlen entgegennehmen. Leicht isoliert testbar (`TurnierStatistikServiceTest`, keine Mocks nötig).

`TurnierMapper.toDto` bekommt eine zweite Signatur (oder ein Overload), die zusätzlich ein `TurnierStatistikService.TurnierStatistik` entgegennimmt und in die drei neuen `TurnierResponseDTO`-Felder einträgt. `TurnierService.getAllTurniere()`/`getTurnier(id)` rufen `turnierStatistikService.berechne(turnier.getSpieler())` auf und reichen das Ergebnis an den Mapper durch.

**Randfall:** 0 Teilnehmer → `durchschnittsRating = null` (Frontend zeigt "–", kein Fehler). Spieler ohne Rating fliegen aus Zähler *und* Nenner — aktuell ist `rating` laut `Spieler.java` ein primitives `double`, kann also technisch nicht `null` sein; der Filter in `TurnierStatistikService` ist trotzdem drin, falls `rating` in einem künftigen Refactor auf `Double`/optional umgestellt wird (siehe offener Glossar-Kandidat "generisches rating-Feld").

## Work Item 4: Pflicht-Validierung bei Neuanlage

**Datei:** `service/TurnierService.java`, Methode `createTurnier`

Kein Bean-Validation-Ansatz (`@NotNull`/`@Valid`) — das Repo hat aktuell keinen `MethodArgumentNotValidException`-Handler in `GlobalExceptionHandler`, würde also eine andere Fehler-Form ausliefern als der Rest der API. Stattdessen manueller Check, konsistent mit dem bestehenden Null-Check ganz oben in derselben Methode:

```java
if (turnierDto.maxTeilnehmer() == null || turnierDto.maxTeilnehmer() < 2) {
    throw new InvalidRequestException("maxTeilnehmer ist Pflicht und muss mindestens 2 sein");
}
```

`turnier.setMaxTeilnehmer(turnierDto.maxTeilnehmer())` danach in den bestehenden Aufbau der `Turnier`-Entity einreihen.

## Work Item 5: Kapazitäts-Blockierung

**Datei:** `service/TurnierService.java`, Methode `addSpielerToTurnier`

```java
public void addSpielerToTurnier(int turnierId, int spielerId){
    Turnier turnier = findTurnierById(turnierId);
    Spieler spieler = spielerRepository.findById(spielerId)
            .orElseThrow(() -> new ResourceNotFoundException("Spieler nicht gefunden"));

    if (turnier.getMaxTeilnehmer() != null
            && turnier.getSpieler().size() >= turnier.getMaxTeilnehmer()) {
        throw new InvalidRequestException("Turnier hat die maximale Teilnehmerzahl bereits erreicht");
    }

    turnier.setTunierspieler(spieler);
    turnierRepository.save(turnier);
}
```

Nur blockierend, wenn `maxTeilnehmer` gesetzt ist — Alt-Turniere ohne Kapazität bleiben unbegrenzt (siehe ADR 0002). Der einzige Frontend-Aufrufer dieses Endpunkts (Button "Teilnehmer hinzufügen" in `TurnierDetailView`) ist aktuell noch deaktiviert — die Verhaltensänderung ist also erstmal unsichtbar, muss aber dokumentiert bleiben, damit die Story, die den Button aktiviert, den 400-Fall von Anfang an mitdenkt.

## Work Item 6: Endpunkt "Turnier abschließen"

**Neue Methode in `service/TurnierService.java`:**

```java
public TurnierResponseDTO turnierAbschliessen(int turnierId){
    Turnier turnier = findTurnierById(turnierId);
    turnier.setStatus(TurnierStatus.ABGESCHLOSSEN);
    return TurnierMapper.toDto(turnierRepository.save(turnier));
}
```

**Neuer Endpunkt in `controller/TurnierController.java`:**

```java
/* Turnier als abgeschlossen markieren: POST /api/Turnier/5/abschliessen */
@PostMapping("/{turnierId}/abschliessen")
public ResponseEntity<TurnierResponseDTO> abschliessen(@PathVariable Integer turnierId){
    return ResponseEntity.ok(turnierService.turnierAbschliessen(turnierId));
}
```

Dedizierter Endpoint statt `PUT /api/Turnier/{id}` mit vollem `TurnierUpdateDTO` — das Frontend muss dafür nicht erst Name/Datum/Ort/Status/`spielerIds` zusammenbauen, nur um ein Feld zu ändern (gleiches Muster wie `addGewinner`/`addVerlierer`). Kein Zurücksetzen auf `GEPLANT`/`LAUFEND` in diesem Zug — bewusst nicht enthalten, siehe "Nächste Schritte".

## Scope

**In scope:** `model/Turnier.java`, `dto/request/turnier/TurnierCreateDTO.java`, `dto/request/turnier/TurnierUpdateDTO.java`, `dto/response/turnier/TurnierResponseDTO.java`, `mapper/TurnierMapper.java`, `service/TurnierService.java`, neue Datei `service/TurnierStatistikService.java`, `controller/TurnierController.java`.

**Out of scope:** Flyway-Migration (siehe ADR 0001), Rückgängig-Endpoint für "abschließen", serverseitiger Status-Filter.

## Test plan

- `TurnierStatistikServiceTest` (neu): leere Liste → `vereinsAnzahl=0`, `durchschnittsRating=null`; Spieler ohne Verein zählen nicht mit; mehrere Spieler im selben Verein zählen als 1.
- `TurnierServiceTest`: `createTurnier` ohne/mit `maxTeilnehmer < 2` → `InvalidRequestException`; `addSpielerToTurnier` bei erreichter Kapazität → `InvalidRequestException`; `addSpielerToTurnier` ohne gesetztes `maxTeilnehmer` weiterhin unbegrenzt; `turnierAbschliessen` setzt Status korrekt.
- `TurnierControllerTest`: neuer `POST /{id}/abschliessen`-Roundtrip (MockMvc); `POST /api/Turnier` mit fehlendem `maxTeilnehmer` → 400 mit dem bestehenden `ErrorResponse`-Body.
- `TurnierMapperTest`: neue Felder werden korrekt durchgereicht, inkl. `null`-Fall für `durchschnittsRating`.

## Done criteria

- [ ] Alle Tests aus dem Testplan grün, bestehende Tests unverändert grün
- [ ] JaCoCo-Coverage on New Code entspricht dem im Repo etablierten Niveau (siehe frühere CI-Läufe, Zielwert war zuletzt >90 %)
- [ ] `POST /api/Turnier` ohne `maxTeilnehmer` liefert 400 mit `ErrorResponse`-Body
- [ ] `POST /api/Turnier/{id}/abschliessen` setzt Status und liefert aktualisiertes DTO
- [ ] `plans/README.md` Status-Zeile für 001 aktualisiert

## Maintenance notes

- Frontend-Gegenstück: `schach-frontend/plans/007-turnier-kapazitaet-und-statistiken.md` — muss nach diesem Plan deployed werden, sonst schlägt dessen Zod-Validierung gegen die neuen Response-Felder fehl.
- `TurnierUpdateDTO.maxTeilnehmer` ist vorbereitet, aber ungenutzt, bis ein Edit-Formular im Frontend existiert — nicht wundern, falls Coverage-Tools das Feld als "nie mit echtem Wert befüllt" markieren.
