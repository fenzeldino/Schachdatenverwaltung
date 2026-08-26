# maxTeilnehmer: nullable Spalte, aber Pflichtfeld bei Neuanlage

## Status

Accepted

## Context

Für die Kapazitätsanzeige im Frontend (Kreisdiagramm "belegte Plätze" in der
Turnier-Übersicht) wird ein neues Feld `maxTeilnehmer` auf `Turnier`
gebraucht. `ddl-auto=update` (siehe `application-prod.properties`) kann bei
einer bereits produktiv befüllten Tabelle keine NOT-NULL-Spalte nachträglich
erzwingen — dasselbe Problem, das schon bei `Turnier.name`/`datum`/`ort`
bestand (siehe Kommentar in `Turnier.java` und das Identity-Problem vom
2026-08-04).

## Decision

Die Spalte bleibt in der Entity nullable (`Integer maxTeilnehmer`, kein
`nullable = false`). Auf Anwendungsebene wird sie trotzdem zur Pflicht
gemacht: `TurnierService.createTurnier` wirft `InvalidRequestException`
(→ 400), wenn `maxTeilnehmer` bei einer Neuanlage fehlt oder kleiner als 2
ist — manueller Check statt Bean-Validation-Annotation, weil
`GlobalExceptionHandler` aktuell keinen
`MethodArgumentNotValidException`-Handler hat und sonst eine andere
Fehler-Form ausgeliefert würde als der Rest der API.

Bestehende Turniere behalten `maxTeilnehmer = null` und haben dauerhaft
keine Kapazitätsanzeige (Frontend zeigt dafür einen Hinweistext statt eines
Kreisdiagramms).

## Consequences

- Jeder Code, der `Turnier.getMaxTeilnehmer()` liest, muss `null` behandeln
  — es gibt keine Garantie durch die Datenbank, nur durch den
  Erstellungs-Pfad.
- Eine echte Migration (z. B. Flyway mit Backfill-Default für Alt-Turniere)
  würde das sauberer lösen, ist aber laut bestehendem Kommentar in
  `application-prod.properties` ein "nächster Schritt", kein aktueller
  Zustand — siehe Nächste-Schritte-Liste in
  [[02 Projekte/Schach-Daten-API.md]].
