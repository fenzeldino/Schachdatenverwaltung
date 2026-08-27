# Vereinszuweisung beim Spieler-Anlegen atomar über SpielerCreateDTO

## Status

Accepted

## Context

Mit der neuen "Spieler anlegen"-Funktion im Frontend (siehe
`schach-frontend/plans/010-spieler-anlegen.md`) soll ein neuer Spieler direkt
beim Anlegen einem Verein zugeordnet werden können — der Miro-Wireframe
"3. Spieler – Detail" zeigt ein Verein-Feld im selben Formular wie Name und
Rating.

Zwei Wege standen zur Wahl:

1. **Zweistufig, ohne Backend-Änderung**: Frontend ruft `POST /api/Spieler`
   auf, danach bei gesetztem Verein zusätzlich
   `PUT /api/Verein/{vereinId}/spieler/{spielerId}` (Endpunkt existiert
   bereits, `VereinService.spielerZuweisen`).
2. **Atomar, mit DTO-Erweiterung**: `SpielerCreateDTO` bekommt ein optionales
   `vereinId`-Feld, `SpielerService.createSpieler` löst es in derselben
   Transaktion auf — analog zum bestehenden `turnierIds`-Feld im selben DTO,
   das genau dieses Muster (Liste von IDs → Repository-Lookup → Zuweisung im
   Service) bereits vorgibt.

Der zweistufige Weg hätte einen möglichen Zwischenzustand geschaffen: schlägt
der zweite Call fehl (Netzwerkfehler, ungültige `vereinId`), existiert der
Spieler bereits — ohne den vom Nutzer gewählten Verein, ohne dass das
Frontend das sauber rückgängig machen könnte.

## Decision

`SpielerCreateDTO` bekommt ein optionales `Integer vereinId`-Feld. Ist es
gesetzt, löst `SpielerService.createSpieler` es serverseitig auf
(`vereinRepository.findById(...)`, `ResourceNotFoundException` bei
unbekannter Id) und setzt den Verein, bevor der Spieler gespeichert wird —
in derselben Transaktion wie das Anlegen selbst.

Der bestehende 4-Parameter-Konstruktor von `SpielerCreateDTO` bleibt über
einen abwärtskompatiblen Kurzform-Konstruktor erhalten (Muster bereits durch
`SpielerResponseDTO`s eigene "Kurzform ohne Vereinsangabe" vorgegeben) —
keine der drei bestehenden Aufrufstellen in den Tests muss geändert werden.

Der bestehende zweistufige Weg
(`PUT /api/Verein/{vereinId}/spieler/{spielerId}`) bleibt unverändert
bestehen — für den Fall, dass ein *bestehender* Spieler nachträglich einem
Verein zugeordnet oder umgehängt wird. Diese ADR betrifft ausschließlich den
Anlege-Zeitpunkt.

## Consequences

- `SpielerCreateDTO` und damit `SpielerController.create` übernehmen eine
  zusätzliche Verantwortung (Vereinszuweisung), die vorher ausschließlich bei
  `VereinController` lag. Das ist eine bewusste Abweichung von einer strikten
  "ein Endpunkt, eine Zuständigkeit"-Aufteilung, zugunsten eines für das
  Frontend einfacheren, atomaren Aufrufs.
- Ein Spieler kann ab sofort nie in einem Zustand "existiert, aber die vom
  Nutzer gewählte Vereinszuweisung ist verlorengegangen" landen.
- Zukünftige DTO-Erweiterungen an `SpielerCreateDTO` sollten denselben
  Kurzform-Konstruktor-Trick nutzen, um die bestehenden Testaufrufstellen
  nicht zu brechen — diese ADR dokumentiert das Muster als Präzedenzfall,
  nicht nur als Einzelfall-Fix.
