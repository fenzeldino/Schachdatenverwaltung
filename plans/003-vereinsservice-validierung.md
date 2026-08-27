# Plan 003: VereinService — Exception-Konsistenz und ZPS-Code-Format-Validierung (Backend)

## Status

- **Priority**: P2
- **Effort**: S
- **Risk**: LOW — ändert Fehlerverhalten eines bestehenden Endpunkts
  (`POST /api/Verein` liefert bei ungültigem Namen/Duplikat jetzt 400 statt
  500), keine Änderung am Erfolgspfad
- **Depends on**: none (Voraussetzung *für* das Frontend-Gegenstück
  `schach-frontend/plans/009-vereinsverwaltung.md`, nicht umgekehrt)
- **Category**: bug + feature
- **Planned at**: 2026-08-27, `/grill-with-docs`-Session (mehrrundige
  Interview-Session mit dem Operator zum "Spieler anlegen"-Button, gemeinsam
  mit dem Frontend-Repo geplant — Runde 2 der Session weitete das ursprüngliche
  Thema auf eine eigenständige Vereinsverwaltung aus)

## Why this matters

Beim Scoping der neuen Vereinsverwaltung (Liste + Anlegen im Frontend, siehe
`schach-frontend/plans/009-vereinsverwaltung.md`) fiel beim Lesen von
`VereinService.createVerein` ein bestehender, bisher folgenloser Bug auf: die
Methode wirft `IllegalArgumentException`, die der `GlobalExceptionHandler`
gar nicht kennt (nur `ResourceNotFoundException` → 404 und
`InvalidRequestException` → 400 sind gemappt). Ein leerer Name oder ein
Namens-Duplikat landet also aktuell als unbehandelter 500 statt als sauberer
400 mit JSON-Fehlerbody — bisher nie aufgefallen, weil `POST /api/Verein`
noch nie von einer Oberfläche aus aufgerufen wurde.

Gleichzeitig wurde in der Grill-Session festgelegt (Q13/Q14), dass der
ZPS-Code — bisher ein reiner, ungeprüfter String — ein festes Format hat
(ein Großbuchstabe + vier Ziffern, z. B. `C0327`, wie im bestehenden
Code-Kommentar in `Verein.java`) und dieses Format sowohl im Frontend als
auch serverseitig durchgesetzt werden soll.

Beides gehört in dieselbe Methode (`createVerein`), deshalb ein gemeinsamer
Plan statt zwei.

## Work Item 1: Exception-Typ korrigieren

**Datei:** `service/VereinService.java`, Methode `createVerein`

```java
// vorher
if (vereinDTO == null || vereinDTO.name() == null || vereinDTO.name().isBlank()) {
    throw new IllegalArgumentException("Ein Verein braucht einen Namen");
}

if (vereinRepository.existsByNameIgnoreCase(vereinDTO.name())) {
    throw new IllegalArgumentException("Verein mit Namen '" + vereinDTO.name() + "' existiert bereits");
}
```

```java
// nachher
if (vereinDTO == null || vereinDTO.name() == null || vereinDTO.name().isBlank()) {
    throw new InvalidRequestException("Ein Verein braucht einen Namen");
}

if (vereinRepository.existsByNameIgnoreCase(vereinDTO.name())) {
    throw new InvalidRequestException("Verein mit Namen '" + vereinDTO.name() + "' existiert bereits");
}
```

Import ergänzen:
`io.github.fenzeldino.schachdatenverwaltung.exception.InvalidRequestException`
(bereits im Projekt vorhanden, u. a. in `TurnierService` genutzt).

## Work Item 2: ZPS-Code-Format serverseitig prüfen

**Datei:** `service/VereinService.java`, Methoden `createVerein` und
`updateVerein`

ZPS-Code ist optional (nullable in DB und DTO) — die Prüfung greift nur,
wenn ein Wert übergeben wurde:

```java
private static final Pattern ZPS_CODE_PATTERN = Pattern.compile("^[A-Z]\\d{4}$");

private void validateZpsCode(String zpsCode) {
    if (zpsCode != null && !zpsCode.isBlank() && !ZPS_CODE_PATTERN.matcher(zpsCode).matches()) {
        throw new InvalidRequestException(
            "ZPS-Code muss aus einem Großbuchstaben gefolgt von vier Ziffern bestehen (z. B. 'C0327')");
    }
}
```

Aufruf in `createVerein` (nach dem Namens-Duplikat-Check) und in
`updateVerein` (vor dem Speichern). `updateVerein` ist nicht Teil der
Grill-Session-Entscheidung (die drehte sich nur ums Anlegen), aber ohne
diese Ergänzung ließe sich das Format über den bestehenden
`PUT /api/Verein/{id}`-Endpunkt trivial umgehen — dieselbe Lücke wie beim
Namens-Duplikat-Check, der ebenfalls nur beim Anlegen greift (siehe
`docs/GLOSSARY.md`/`CONTEXT.md`, Namens-Duplikat ist eine bekannte,
bewusste Lücke bei `updateVerein`; das Format soll das nicht sein).

## Scope

**In scope:** `service/VereinService.java` (`createVerein`, `updateVerein`).

**Out of scope:** Namens-Duplikat-Check auf `updateVerein` nachziehen (nicht
Teil dieser Session, bestehende separate Lücke), `VereinController`
(keine Signaturänderung nötig — der neue Exception-Typ wird vom bereits
vorhandenen `GlobalExceptionHandler` automatisch gemappt).

## Test plan

- `VereinServiceTest`:
  - `createVerein` mit leerem/`null`-Namen → `InvalidRequestException`
    (Assertion von `IllegalArgumentException` auf `InvalidRequestException`
    umstellen)
  - `createVerein` mit Namens-Duplikat → `InvalidRequestException` (gleiche
    Umstellung)
  - `createVerein` mit ungültigem ZPS-Code (`"c0327"` klein, `"C032"` zu
    kurz, `"C03270"` zu lang, `"0327C"` falsche Reihenfolge) →
    `InvalidRequestException`
  - `createVerein` mit gültigem ZPS-Code (`"C0327"`) → erfolgreich
  - `createVerein` mit `zpsCode = null` → weiterhin erfolgreich (Feld bleibt
    optional)
  - `updateVerein` mit ungültigem ZPS-Code → `InvalidRequestException`
- `VereinControllerTest`: bestehender `create_shouldReturnCreatedVerein`-Test
  bleibt unverändert grün (Controller-Verhalten ändert sich nicht, nur was
  der gemockte Service wirft). Kein neuer Test zwingend nötig.
- `GlobalExceptionHandlerTest`: kein neuer Test nötig — der 400-Pfad für
  `InvalidRequestException` ist dort bereits generisch abgedeckt.

## Done criteria

- [ ] Alle Tests aus dem Testplan grün, bestehende `VereinServiceTest`-Fälle
      auf `InvalidRequestException` umgestellt
- [ ] `POST /api/Verein` liefert bei leerem Namen/Duplikat/ungültigem
      ZPS-Code 400 mit JSON-Fehlerbody statt 500
- [ ] `plans/README.md` Status-Zeile für 003 aktualisiert

## Maintenance notes

- Frontend-Gegenstück: `schach-frontend/plans/009-vereinsverwaltung.md` —
  sollte nach diesem Plan deployed werden, sonst zeigt das neue
  `VereinFormView` bei Duplikat/ungültigem Format eine generische
  "Server-Fehler"-Meldung statt der konkreten Backend-Nachricht.
- Bereitet `plans/004-spieler-anlegen-vereinszuweisung.md` vor (Verein muss
  zum Zeitpunkt der Spieler-Vereinszuweisung bereits über ein sauberes,
  validiertes `VereinResponseDTO` verfügen — inhaltlich unabhängig, aber
  gleiche Session).
