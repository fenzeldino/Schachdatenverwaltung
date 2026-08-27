# Schachdatenverwaltung

Backend-API für die Verwaltung von Schachturnieren, Spielern, MatchUps und
Vereinen (Spring Boot, PostgreSQL).

## Language

**Turnier**:
Container aus mehreren Spielern und MatchUps mit eigener Rating-Berechnung (Dresden-Wertung, Elo).

**Platz** (Teilnehmerplatz):
Eine von `maxTeilnehmer` vorgegebene Kapazitätseinheit für genau einen Spieler in einem Turnier. 1 Platz = 1 Spieler, keine Team- oder Vereinsplätze.
_Avoid_: Slot, Kapazitätseinheit

**maxTeilnehmer**:
Die bei der Turnier-Neuanlage festgelegte maximale Anzahl an Plätzen. Pflicht bei Neuanlage (mindestens 2), aber nullable in der Datenbank für Alt-Turniere ohne diesen Wert (siehe ADR 0001).
_Avoid_: Kapazität, maxPlätze, Obergrenze, Limit

**teilnehmerAnzahl**:
Die tatsächliche, aktuell belegte Anzahl an Plätzen. Kein eigenes DB-Feld, sondern abgeleitet aus `spielerIds.size()`.
_Avoid_: Ist-Teilnehmerzahl, belegte Plätze

**Aktives Turnier**:
Ein Turnier mit Status `GEPLANT` oder `LAUFEND`.
_Avoid_: laufendes Turnier (mehrdeutig mit dem Status-Wert `LAUFEND`)

**Abgeschlossenes Turnier**:
Ein Turnier mit Status `ABGESCHLOSSEN`. Wird ausschließlich über den eigenen Endpunkt `POST /api/Turnier/{id}/abschliessen` gesetzt, nicht über das generische Update (siehe ADR 0002).
_Avoid_: beendetes Turnier, altes Turnier

**MatchUp**:
Eine Begegnung zwischen genau zwei Spielern mit optionalem Gewinner.

**Gewinner** / **Verlierer**:
Der Spieler, der ein MatchUp für sich entschieden bzw. verloren hat. `Verlierer` ist im Modell aktuell kein eigenes Feld, sondern wird aus `Gewinner` hergeleitet — bekannter Bug, siehe Nächste-Schritte-Liste im Projekt.

**Spieler**:
Person mit einem Rating (aktuell ein generisches `rating`-Feld statt getrennter DWZ-/Elo-Werte — offener Glossar-Kandidat), die an Turnieren und MatchUps teilnimmt.

**Verein**:
Ein Schachverein, dem Mitglieder zugeordnet sind. Vereinsnamen müssen
eindeutig sein (`VereinService.createVerein` prüft das), im Gegensatz zu
`Spieler.name` — echte Personen können denselben Namen tragen, Vereine
(bewusst) nicht.

**ZPS-Code**:
Kennung eines Vereins bei der Zentralen Personenstammdatei des Deutschen
Schachbundes. Format: ein Großbuchstabe gefolgt von vier Ziffern (z. B.
`C0327`). Optionales Feld bei `Verein`, aber falls gesetzt, seit Plan 003
serverseitig gegen dieses Format validiert (`createVerein`/`updateVerein`).
_Avoid_: Vereinscode, Vereinskennung

**Mitglied**:
Person mit Vereinszugehörigkeit, unabhängig von der Turnier-Teilnahme als Spieler. Noch nicht über eigene Endpunkte angebunden.
