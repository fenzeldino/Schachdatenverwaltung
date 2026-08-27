# Plan 004: Spieler anlegen — Validierung und atomare Vereinszuweisung (Backend)

## Status

- **Priority**: P1
- **Effort**: M
- **Risk**: MEDIUM — ändert das Verhalten eines bestehenden, produktiv
  genutzten Endpunkts (`POST /api/Spieler` liefert jetzt in drei weiteren
  Fällen 400 statt eines stillen `null`-Bodys bei 201), DTO-Erweiterung ist
  aber additiv und abwärtskompatibel (siehe Work Item 1)
- **Depends on**: none (Voraussetzung *für* das Frontend-Gegenstück
  `schach-frontend/plans/010-spieler-anlegen.md`, nicht umgekehrt)
- **Category**: bug + feature
- **Planned at**: 2026-08-27, `/grill-with-docs`-Session (mehrrundige
  Interview-Session mit dem Operator zum "Spieler anlegen"-Button in
  `SpielerListView`, gemeinsam mit dem Frontend-Repo geplant)

Eine begleitende ADR ist bereits im Repo:
`docs/adr/0004-vereinszuweisung-atomar-beim-spieler-anlegen.md`.

## Why this matters

Der "+ Spieler anlegen"-Button in `SpielerListView` ist im Frontend seit
jeher hart deaktiviert. Das Backend kann Spieler bereits vollständig anlegen
(`POST /api/Spieler`), hat dabei aber zwei reale Lücken, die bisher
folgenlos blieben, weil kein Aufrufer existierte:

1. **Keine Validierung.** `SpielerService.createSpieler` prüft nur, ob das
   gesamte DTO `null` ist (`System.out.println(...)` + `return null` statt
   eines Fehlers) — leerer Name, negatives Rating oder negatives Alter
   werden anstandslos gespeichert.
2. **Keine Vereinszuweisung möglich.** `SpielerCreateDTO` kennt kein
   `vereinId`-Feld. Der Miro-Wireframe "3. Spieler – Detail" zeigt aber ein
   Verein-Feld, und der Operator hat entschieden (Grill-Session Q3/Q8), dass
   ein neuer Spieler direkt beim Anlegen einem Verein zugeordnet werden
   können soll — atomar, nicht als zweiter, potenziell fehlschlagender
   Schritt über den bestehenden `PUT /api/Verein/{vereinId}/spieler/{spielerId}`-Endpunkt.

## Work Item 1: `SpielerCreateDTO` um optionales `vereinId` erweitern

**Datei:** `dto/request/spieler/SpielerCreateDTO.java`

```java
public record SpielerCreateDTO(
                               String Name,
                               Double rating,
                               Integer alter,
                               List<Integer> turnierIds,
                               Integer vereinId) {

    public SpielerCreateDTO {
    }

    // Abwärtskompatibler Kurzform-Konstruktor: alle drei bestehenden
    // Aufrufstellen (SpielerServiceTest, SpielerControllerTest,
    // SpielerMapperTest) nutzen den bisherigen 4-Parameter-Konstruktor und
    // bleiben unverändert kompilierbar — exakt das Muster, das
    // SpielerResponseDTO für seine eigene "Kurzform ohne Vereinsangabe"
    // bereits vorgemacht hat.
    public SpielerCreateDTO(String Name, Double rating, Integer alter, List<Integer> turnierIds) {
        this(Name, rating, alter, turnierIds, null);
    }

    @Override
    public String Name() {
        return Name;
    }

    @Override
    public Double rating() {
        return rating;
    }

    @Override
    public Integer alter() {
        return alter;
    }

    @Override
    public List<Integer> turnierIds() {
        return turnierIds;
    }

    @Override
    public Integer vereinId() {
        return vereinId;
    }
}
```

**Wichtig:** kein neuer Test in den drei bestehenden Testdateien muss
angepasst werden — das ist der ganze Punkt des Kurzform-Konstruktors.

## Work Item 2: Validierung + Vereinszuweisung in `SpielerService.createSpieler`

**Datei:** `service/SpielerService.java`

```java
@Transactional
public SpielerResponseDTO createSpieler(SpielerCreateDTO spielerDto){

    if (spielerDto == null) {
        throw new InvalidRequestException("Spieler-Daten dürfen nicht leer sein");
    }
    if (spielerDto.Name() == null || spielerDto.Name().isBlank()) {
        throw new InvalidRequestException("Ein Spieler braucht einen Namen");
    }
    if (spielerDto.rating() != null && spielerDto.rating() < 0) {
        throw new InvalidRequestException("Rating darf nicht negativ sein");
    }
    if (spielerDto.alter() != null && spielerDto.alter() < 0) {
        throw new InvalidRequestException("Alter darf nicht negativ sein");
    }

    Spieler spieler = SpielerMapper.toEntity(spielerDto);

    List<Turnier> Turniere = turnierRepository.findAllById(spielerDto.turnierIds());
    spieler.setTurnier(Turniere);

    if (spielerDto.vereinId() != null) {
        Verein verein = vereinRepository.findById(spielerDto.vereinId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Verein mit Id: " + spielerDto.vereinId() + " wurde nicht gefunden"));
        spieler.setVerein(verein);
    }

    spielerRepository.save(spieler);
    return SpielerMapper.toDto(spieler);
}
```

**Konstruktor-Änderung:** `SpielerService` braucht `VereinRepository` als
dritte Abhängigkeit:

```java
private final SpielerRepository spielerRepository;
private final TurnierRepository turnierRepository;
private final VereinRepository vereinRepository;

public SpielerService(SpielerRepository spielerRepository,
                       TurnierRepository turnierRepository,
                       VereinRepository vereinRepository){
    this.spielerRepository = spielerRepository;
    this.turnierRepository = turnierRepository;
    this.vereinRepository = vereinRepository;
}
```

`SpielerServiceTest` mockt den Konstruktor über `@InjectMocks` — ein neues
`@Mock private VereinRepository vereinRepository;`-Feld reicht, Mockito
verdrahtet automatisch.

**Bewusst kein Namens-Duplikat-Check** (anders als bei `VereinService`,
siehe Plan 003) — echte Personen können denselben Namen tragen, ein
Duplikat-Check auf `Spieler.name` würde einen legitimen zweiten
gleichnamigen Spieler blockieren (Grill-Session Q10). Im `CONTEXT.md` als
bewusste Asymmetrie zu `Verein` dokumentiert.

## Scope

**In scope:** `dto/request/spieler/SpielerCreateDTO.java`,
`service/SpielerService.java` (`createSpieler`, Konstruktor).

**Out of scope:** DWZ-/Elo-Rating-Split (bleibt generisches `rating`-Feld,
Grill-Session Q2), Namens-Duplikat-Check bei Spieler (Q10, bewusst nicht),
Turnier-Auswahl beim Anlegen (Q5, DTO-Feld `turnierIds` bleibt unverändert
nutzbar, aber das neue Frontend-Formular sendet dafür immer eine leere
Liste), `updateSpieler` (hat dieselben Validierungslücken, aber nicht Teil
dieser Session — der "Spieler anlegen"-Button betrifft nur `createSpieler`).

## Test plan

- `SpielerServiceTest`:
  - `createSpieler` mit `null`-DTO → `InvalidRequestException`
  - `createSpieler` mit leerem/`null`-Namen → `InvalidRequestException`
  - `createSpieler` mit negativem Rating → `InvalidRequestException`
  - `createSpieler` mit negativem Alter → `InvalidRequestException`
  - `createSpieler` mit gültigem `vereinId` → `vereinRepository.findById`
    wird aufgerufen, `SpielerResponseDTO.vereinId()`/`vereinName()` sind
    befüllt
  - `createSpieler` mit `vereinId` für nicht existierenden Verein →
    `ResourceNotFoundException`
  - `createSpieler` mit `vereinId = null` (Kurzform-Konstruktor oder
    explizit `null`) → kein `vereinRepository`-Aufruf, Spieler wird ohne
    Verein angelegt (bestehendes Verhalten bleibt erhalten)
  - bestehender Erfolgsfall (`createSpieler_shouldCreateSpielerWithTurniere`)
    bleibt unverändert grün (nutzt den Kurzform-Konstruktor, `vereinId`
    implizit `null`)
- `SpielerControllerTest`: kein neuer Test zwingend nötig
  (Controller-Signatur unverändert, bestehender Test nutzt
  Kurzform-Konstruktor und bleibt grün).
- `SpielerMapperTest`: unverändert grün (`SpielerMapper.toEntity` liest
  weiterhin nur Name/Rating/Alter, `vereinId` wird im Service aufgelöst,
  nicht im Mapper).

## Done criteria

- [ ] Alle Tests aus dem Testplan grün, bestehende Tests unverändert grün
- [ ] `POST /api/Spieler` lehnt leeren Namen, negatives Rating, negatives
      Alter mit 400 ab
- [ ] `POST /api/Spieler` mit gültigem `vereinId` liefert einen Spieler mit
      befülltem `vereinName` in der Antwort
- [ ] `POST /api/Spieler` mit unbekanntem `vereinId` liefert 404
- [ ] `docs/adr/0004-vereinszuweisung-atomar-beim-spieler-anlegen.md`
      existiert (bereits während der Grill-Session angelegt)
- [ ] `plans/README.md` Status-Zeile für 004 aktualisiert

## Maintenance notes

- Frontend-Gegenstück: `schach-frontend/plans/010-spieler-anlegen.md` — setzt
  zusätzlich `schach-frontend/plans/009-vereinsverwaltung.md` voraus (braucht
  `vereinApi.getAll()` fürs Dropdown). Muss nach diesem Plan deployed werden,
  sonst liefert `POST /api/Spieler` mit `vereinId` einen 404, den das
  Frontend nicht erwartet.
- `updateSpieler` hat dieselben Validierungslücken (kein Namens-/Rating-/
  Alter-Check) wie `createSpieler` vor diesem Plan — bewusst nicht mit
  angefasst, da außerhalb des "Spieler anlegen"-Scopes. Eigener Plan bei
  Bedarf.
