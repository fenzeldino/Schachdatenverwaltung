# Plan 002: Teilnehmer hinzufügen — Absicherung von `addSpielerToTurnier` (Backend)

## Status

- **Priority**: P1
- **Effort**: S
- **Risk**: MEDIUM — ändert das Verhalten eines bestehenden, produktiv
  genutzten Endpunkts (`addSpielerToTurnier` liefert jetzt in zwei weiteren
  Fällen 400 statt 204)
- **Depends on**: none (Voraussetzung *für* das Frontend-Gegenstück
  `plans/008-teilnehmer-hinzufuegen.md` im Repo `schach-frontend`, nicht
  umgekehrt)
- **Category**: bug + feature
- **Planned at**: 2026-08-27, `/grill-with-docs`-Session (mehrrundige
  Interview-Session mit dem Operator, gemeinsam mit dem Frontend-Repo
  geplant — Fortsetzung der Turnier-Kapazität-Session vom 2026-08-26)

Eine begleitende ADR ist bereits im Repo:
`docs/adr/0003-teilnehmer-nur-bei-geplantem-turnier-hinzufuegen.md`.
Begriffsklärung ("Teilnehmer" als eigener Begriff, abgegrenzt von "Spieler")
liegt im Frontend-`CONTEXT.md` — dieses Repo definiert `Teilnehmer` nicht
gesondert, da der Endpunkt weiterhin ausschließlich mit `Spieler`-Entities
arbeitet.

## Why this matters

Das Frontend aktiviert den bisher deaktivierten "+ Teilnehmer
hinzufügen"-Button in `TurnierDetailView`. Beim Review von
`TurnierService.addSpielerToTurnier` für diese Freischaltung sind zwei reale
Lücken aufgefallen, die vorher folgenlos blieben, weil der einzige Aufrufer
(der Button) deaktiviert war:

1. **Kein Duplikat-Schutz**: `Turnier.setTunierspieler(spieler)` ruft
   `Spieler.add(spieler)` ohne jede Prüfung auf — derselbe Spieler lässt sich
   beliebig oft zum selben Turnier hinzufügen. Ein Duplikat würde
   `TurnierStatistikService.berechne(...)` verfälschen (Vereinsanzahl und
   Ø-Rating zählen den Spieler doppelt).
2. **Kein Status-Gate**: Teilnehmer lassen sich aktuell auch zu einem
   `LAUFEND`en oder `ABGESCHLOSSEN`en Turnier hinzufügen — der Operator hat
   festgelegt, dass das nur während der Planungsphase (`GEPLANT`) erlaubt sein
   soll (siehe ADR 0003).

Beide Prüfungen kommen in dieselbe Methode, die auch die bestehende
`maxTeilnehmer`-Kapazitätsprüfung (ADR 0002) enthält — konsistente Stelle,
keine neue Verantwortlichkeit.

## Work Item 1: Status-Gate

**Datei:** `service/TurnierService.java`, Methode `addSpielerToTurnier`

Zuerst geprüft (fundamentalste Voraussetzung, siehe ADR 0003):

```java
if (turnier.getStatus() != TurnierStatus.GEPLANT) {
    throw new InvalidRequestException(
        "Turnier ist nicht mehr in Planung, es können keine Teilnehmer mehr hinzugefügt werden");
}
```

Bewusst `!=` statt eine Whitelist auf `GEPLANT` — `status == null` (Alt-Turniere
ohne gepflegten Status) fällt damit auch unter die Sperre. Das ist restriktiver
als das bestehende "Aktives Turnier"-Konzept im Frontend (das `null` als aktiv
behandelt), aber hier bewusst so: im Zweifel nicht erlauben, nicht nachträglich
Alt-Turniere versehentlich wieder befüllbar machen.

## Work Item 2: Duplikat-Guard

**Datei:** `service/TurnierService.java`, Methode `addSpielerToTurnier`

Nach dem Status-Gate, vor der bestehenden Kapazitätsprüfung:

```java
if (turnier.getSpieler().contains(spieler)) {
    throw new InvalidRequestException("Spieler ist bereits Teilnehmer dieses Turniers");
}
```

`List<Spieler>.contains(...)` verlässt sich auf `Spieler.equals()` — prüfen,
ob `Spieler` `equals()`/`hashCode()` überschreibt (JPA-Entity-Default wäre
Referenzgleichheit, was hier wegen des durch `spielerRepository.findById(...)`
frisch geladenen Objekts trotzdem funktioniert, aber explizit verifizieren statt
anzunehmen). Falls nicht überschrieben: alternativ über
`turnier.getSpieler().stream().anyMatch(s -> s.getSpielerId() == spieler.getSpielerId())`
prüfen, um nicht von Entity-`equals()`-Verhalten abhängig zu sein.

## Zusammengeführte Methode (Endzustand)

```java
@Transactional
public void addSpielerToTurnier(int turnierId, int spielerId){
    Turnier turnier = findTurnierById(turnierId);
    Spieler spieler = spielerRepository.findById(spielerId)
            .orElseThrow(() -> new ResourceNotFoundException("Spieler nicht gefunden"));

    if (turnier.getStatus() != TurnierStatus.GEPLANT) {
        throw new InvalidRequestException(
            "Turnier ist nicht mehr in Planung, es können keine Teilnehmer mehr hinzugefügt werden");
    }

    boolean bereitsTeilnehmer = turnier.getSpieler().stream()
            .anyMatch(s -> s.getSpielerId() == spieler.getSpielerId());
    if (bereitsTeilnehmer) {
        throw new InvalidRequestException("Spieler ist bereits Teilnehmer dieses Turniers");
    }

    if (turnier.getMaxTeilnehmer() != null
            && turnier.getSpieler().size() >= turnier.getMaxTeilnehmer()) {
        throw new InvalidRequestException("Turnier hat die maximale Teilnehmerzahl bereits erreicht");
    }

    turnier.setTunierspieler(spieler);
    turnierRepository.save(turnier);
}
```

Reihenfolge bewusst: Status vor Duplikat vor Kapazität — je fundamentaler die
Voraussetzung, desto früher die Prüfung, damit die Fehlermeldung immer den
naheliegendsten Grund nennt (ein abgeschlossenes Turnier ist der relevantere
Fehler als "schon drin", selbst falls beides zuträfe).

## Scope

**In scope:** `service/TurnierService.java` (nur `addSpielerToTurnier`).

**Out of scope:** Änderungen an `Turnier.setTunierspieler` selbst (bleibt
dumm/deep-module-konform — die Validierung gehört in den Service, nicht ins
Model), Endpunkt/Controller (keine Signaturänderung nötig, bleibt
`ResponseEntity<Void>` mit 204 im Erfolgsfall), Migration bestehender
Duplikate in der Datenbank (falls welche existieren — nicht bekannt, nicht
Teil dieses Plans; bei Bedarf separat prüfen).

## Test plan

- `TurnierServiceTest`:
  - `addSpielerToTurnier` bei Status `LAUFEND` → `InvalidRequestException`
  - `addSpielerToTurnier` bei Status `ABGESCHLOSSEN` → `InvalidRequestException`
  - `addSpielerToTurnier` bei Status `null` → `InvalidRequestException`
  - `addSpielerToTurnier` mit bereits enthaltenem Spieler (Status `GEPLANT`,
    Kapazität nicht erreicht) → `InvalidRequestException`, kein zweiter Eintrag
  - bestehender Erfolgsfall (`addSpielerToTurnier_byId_shouldAddSpielerToTurnier`)
    muss weiterhin grün bleiben — Test-Fixture dort auf `status = GEPLANT`
    setzen, falls noch kein Status gesetzt wird (sonst bricht er neu am
    Status-Gate)
  - bestehender Kapazitäts-Test (ADR 0002) muss weiterhin grün bleiben,
    Fixture ebenfalls auf `GEPLANT` prüfen
- `TurnierControllerTest`: kein neuer Test zwingend nötig (Controller-Signatur
  unverändert), aber ein Roundtrip-Test für den 400-Fall bei `LAUFEND`/
  Duplikat schadet nicht und spiegelt das bestehende Muster für den
  Kapazitäts-400-Fall.

## Done criteria

- [ ] Alle Tests aus dem Testplan grün, bestehende Tests unverändert grün
      (insbesondere die beiden bestehenden `addSpielerToTurnier`-Tests, deren
      Fixtures ggf. um `status = GEPLANT` ergänzt werden müssen)
- [ ] `addSpielerToTurnier` lehnt Status ≠ `GEPLANT` mit 400 ab
- [ ] `addSpielerToTurnier` lehnt bereits vorhandene Teilnehmer mit 400 ab
- [ ] `docs/adr/0003-teilnehmer-nur-bei-geplantem-turnier-hinzufuegen.md`
      existiert (bereits während der Grill-Session angelegt)
- [ ] `plans/README.md` Status-Zeile für 002 aktualisiert

## Maintenance notes

- Frontend-Gegenstück: `schach-frontend/plans/008-teilnehmer-hinzufuegen.md`
  — muss nach diesem Plan deployed werden, sonst laufen Nutzer beim
  Hinzufügen-Versuch gegen die alten, ungeschützten Endpunkt-Zusicherungen
  (das Frontend geht z. B. davon aus, dass ein Duplikat-Versuch über die
  eigene UI praktisch nicht vorkommt, weil es bereits herausgefiltert wird —
  der Backend-Guard ist das Sicherheitsnetz für Race Conditions/direkte
  API-Aufrufe, nicht der Regelfall).
- Der bekannte `addVerlierer`-Bug (siehe
  `02 Projekte/Schach-Daten-API.md` im Vault) ist unabhängig von diesem Plan
  und bleibt unangetastet.
