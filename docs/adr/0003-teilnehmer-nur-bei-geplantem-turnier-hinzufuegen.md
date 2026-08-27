# Teilnehmer nur bei Turnier-Status GEPLANT hinzufügbar

## Status

Accepted

## Context

Mit der neuen "Teilnehmer hinzufügen"-Funktion im Frontend (siehe
`schach-frontend/plans/008-teilnehmer-hinzufuegen.md`) wurde festgelegt, dass
sich Teilnehmer nur zu einem Turnier hinzufügen lassen sollen, solange es
noch in der Planungsphase ist. Bisher prüfte `addSpielerToTurnier` nur die
Kapazität (`maxTeilnehmer`, siehe ADR 0002), aber nicht den Turnier-Status —
ein Spieler ließe sich also auch zu einem bereits `LAUFEND`en oder
`ABGESCHLOSSEN`en Turnier hinzufügen.

## Decision

`TurnierService.addSpielerToTurnier` wirft jetzt zusätzlich
`InvalidRequestException` (→ HTTP 400), wenn `turnier.getStatus() !=
TurnierStatus.GEPLANT`. Das gilt für `LAUFEND` und `ABGESCHLOSSEN` gleichermaßen
sowie für `status = null` (Alt-Turniere ohne gepflegten Status — im Zweifel
restriktiv, nicht permissiv). Die Regel wird bewusst serverseitig durchgesetzt,
nicht nur im Frontend versteckt (Button/Route nur bei `GEPLANT` erreichbar) —
sonst wäre sie über einen direkten API-Aufruf trivial umgehbar, sobald es
einen zweiten Konsumenten dieser API gibt.

## Consequences

- Jeder Aufrufer von `POST /api/Turnier/{id}/spieler/{spielerId}` muss mit
  einer 400-Antwort rechnen, wenn das Zielturnier nicht mehr `GEPLANT` ist —
  zusätzlich zum bereits bestehenden 400-Fall bei erreichter Kapazität (ADR
  0002) und dem neuen Duplikat-Fall (siehe `plans/002-teilnehmer-hinzufuegen.md`).
- Ein einmal auf `LAUFEND` oder `ABGESCHLOSSEN` gesetztes Turnier kann seine
  Teilnehmerliste über diesen Endpunkt nicht mehr verändern lassen — das ist
  beabsichtigt, nicht nur eine Falle für Aufrufer.
