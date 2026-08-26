# addSpielerToTurnier blockiert bei erreichter Kapazität

## Status

Accepted

## Context

Mit `maxTeilnehmer` (siehe ADR 0001) gibt es erstmals eine Obergrenze für
Turnier-Teilnehmer. Bisher konnte `POST /api/Turnier/{id}/spieler/{spielerId}`
beliebig viele Spieler zu einem Turnier hinzufügen, ohne jede Prüfung.

## Decision

`TurnierService.addSpielerToTurnier` wirft jetzt `InvalidRequestException`
(→ HTTP 400) statt den Spieler stillschweigend hinzuzufügen, sobald
`turnier.getSpieler().size() >= maxTeilnehmer` — nur wenn `maxTeilnehmer`
gesetzt ist. Alt-Turniere ohne Kapazitätswert bleiben unbegrenzt, wie
bisher. Das ist eine echte Verhaltensänderung eines bestehenden,
produktiv genutzten Endpunkts, kein rein additives Feature.

## Consequences

- Jeder Aufrufer von `POST /api/Turnier/{id}/spieler/{spielerId}` muss ab
  jetzt mit einer 400-Antwort rechnen, wo vorher immer 204 kam.
- Im Frontend (`schach-frontend`) ist der einzige Aufrufer dieses
  Endpunkts — der Button "Teilnehmer hinzufügen" in `TurnierDetailView` —
  aktuell noch deaktiviert ("folgt in einer späteren Story"). Die
  Verhaltensänderung ist also praktisch unsichtbar, bis dieser Button
  aktiviert wird, aber bereits Teil des API-Vertrags. Die Story, die den
  Button aktiviert, muss den 400-Fall explizit behandeln — vermerkt in
  [[02 Projekte/Schach-Frontend.md]] und im dortigen Frontend-Plan
  ([[02 Projekte/Turnier-Kapazität und Statistiken/Frontend-Plan.md]]).
- Genau diese Art unkommunizierter API-Verhaltensänderung war schon einmal
  ein offener Punkt zwischen den beiden Repos (drei Verhaltensänderungen
  aus dem Deep-Module-Refactor vom 2026-08-25, siehe
  [[02 Projekte/Schach-Daten-API.md]]) — dieses ADR soll genau das diesmal
  vermeiden.
