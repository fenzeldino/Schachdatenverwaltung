# Schachdatenverwaltung

Backend-API zur Verwaltung von Schachspielern, Turnieren und Partien (MatchUps), inklusive eigener Rating-Berechnung (Elo & Dresdner Methode). Gebaut mit Spring Boot, PostgreSQL und einer vollständigen CI/CD-Pipeline über GitHub Actions.

## Tech-Stack

- **Java 25** (Eclipse Temurin)
- **Spring Boot 4.0.6** – Spring Data JPA, Spring Data REST
- **PostgreSQL** (Produktion) / **H2** (Konsole für lokale Zwecke)
- **JUnit 5 + Mockito** für Tests
- **JaCoCo** für Test-Coverage
- **SonarQube Cloud** für statische Codeanalyse
- **Docker** für Deployment
- **GitHub Actions** für CI/CD, mit self-hosted Runner
- **Graphify** für automatisch generierte Architektur-Graphen (`graphify-out/`)

## Domänenmodell

Das Projekt hat zwei Bereiche:

**Turnierbetrieb (über die API nutzbar):**
- `Spieler` – Teilnehmer mit Name, Rating und Alter
- `Turnier` – hat eine Liste von Spielern und MatchUps
- `MatchUp` – eine Partie zwischen zwei Spielern, mit optionalem Gewinner

**Vereinsverwaltung (Domain-Modell vorhanden, noch nicht über REST exponiert):**
- `Person` – Basisklasse, `Mitglied extends Person` – Vereinsmitglied mit Elo-Wertung
- `SchachVerein` – verwaltet Mitglieder
- `Status`, `Geschlecht`, `GewOdVer` – Enums für Mitgliedsstatus, Geschlecht, Spielergebnis

**Rating-Berechnung:**
- `RatingCalculator` (Interface) – implementiert von `TurnierService`
- Elo-Berechnung (`EloBerehcnung`) und Dresdner Berechnung (`DresdenCalculator`) mit eigener `DwzMatrix` (Deutsche Wertungszahl)

## Architektur

Klassischer Schichtenaufbau, jede Ressource (Spieler, MatchUp, Turnier) folgt dem gleichen Muster:

```
Controller → Service → Mapper → Repository → Datenbank
```

- **DTOs** sind nach Request/Response getrennt (`dto/request/<ressource>`, `dto/response/<ressource>`), damit interne Entities nie direkt über die API nach außen gehen
- **Mapper** wandeln zwischen Entity und DTO um
- Wo Endpunkte bestehende Entities referenzieren (z.B. einen Spieler zu einem Turnier hinzufügen), nehmen sie die **ID im Pfad** entgegen und laden die Entity intern über das Repository – nie eine rohe Entity im Request-Body

Einen visuellen, automatisch generierten Überblick über alle Klassen und ihre Beziehungen gibt es in `graphify-out/GRAPH_REPORT.md` bzw. `graphify-out/graph.html` (wird nach jedem Commit per Git-Hook neu gebaut).

## API-Endpunkte

### Spieler (`/api/Spieler`)

| Methode | Pfad | Beschreibung |
|---|---|---|
| `POST` | `/api/Spieler` | Spieler anlegen |
| `GET` | `/api/Spieler/getAllSpieler` | Alle Spieler |
| `GET` | `/api/Spieler/{id}` | Einzelnen Spieler abrufen |
| `PUT` | `/api/Spieler/{id}` | Spieler aktualisieren |
| `DELETE` | `/api/Spieler/{id}` | Spieler löschen |

### MatchUp (`/api/matchup`)

| Methode | Pfad | Beschreibung |
|---|---|---|
| `POST` | `/api/matchup` | MatchUp anlegen |
| `GET` | `/api/matchup/getAllMatchUps` | Alle MatchUps |
| `PUT` | `/api/matchup/{id}` | MatchUp aktualisieren |
| `DELETE` | `/api/matchup/{id}` | MatchUp löschen |
| `POST` | `/api/matchup/{matchUpId}/gewinner/{spielerId}` | Gewinner setzen |
| `POST` | `/api/matchup/{matchUpId}/verlierer/{spielerId}` | Verlierer setzen |

### Turnier (`/api/Turnier`)

| Methode | Pfad | Beschreibung |
|---|---|---|
| `POST` | `/api/Turnier` | Turnier anlegen |
| `GET` | `/api/Turnier/getAllTurniere` | Alle Turniere |
| `GET` | `/api/Turnier/{id}` | Einzelnes Turnier abrufen |
| `PUT` | `/api/Turnier/{id}` | Turnier aktualisieren |
| `DELETE` | `/api/Turnier/{id}` | Turnier löschen |
| `GET` | `/api/Turnier/{turnierId}/spieler?ids=1,2,3` | Spieler eines Turniers gefiltert nach IDs |
| `GET` | `/api/Turnier/{turnierId}/matchups?ids=8,9` | MatchUps eines Turniers gefiltert nach IDs |
| `POST` | `/api/Turnier/{turnierId}/spieler/{spielerId}` | Bestehenden Spieler zum Turnier hinzufügen |
| `POST` | `/api/Turnier/{turnierId}/matchups/{matchUpId}/link` | Bestehendes MatchUp mit dem Turnier verknüpfen |
| `POST` | `/api/Turnier/{turnierId}/matchups/neu/{spieler1Id}/{spieler2Id}` | Neues MatchUp zwischen zwei Spielern erstellen |
| `POST` | `/api/Turnier/{turnierId}/matchups/{matchId}/dresden` | Rating-Berechnung nach Dresdner Methode auslösen |
| `POST` | `/api/Turnier/{turnierId}/matchups/{matchId}/elo` | Rating-Berechnung nach Elo auslösen |

## Lokal starten

Voraussetzungen: JDK 25, eine erreichbare PostgreSQL-Instanz.

```bash
./mvnw spring-boot:run
```

Datenbankverbindung wird über `src/main/resources/application.properties` konfiguriert.

> **Achtung:** Das DB-Passwort steht dort aktuell im Klartext im Repository. Für den produktiven Einsatz sollte das über eine Umgebungsvariable oder ein Secret gelöst werden (siehe Roadmap).

## Tests

```bash
./mvnw test
```

Reine Unit-Tests mit JUnit 5 + Mockito (kein MockMvc/Spring-Context nötig, außer beim Application-Context-Test). Jede Ressource hat Tests auf Service-, Mapper- und Controller-Ebene.

## CI/CD

Bei jedem Push auf `main` (`.github/workflows/deploy.yml`, self-hosted Runner):

1. Tests ausführen (`./mvnw clean verify`)
2. JaCoCo Coverage-Report erzeugen
3. SonarQube-Analyse (Quality Gate: Coverage on New Code ≥ 80%)
4. Docker-Image bauen
5. Container auf dem Home Server neu starten

## Docker

```bash
docker build -t schach-api .
docker run -p 8080:8080 schach-api
```

## Roadmap / bekannte offene Punkte

- [ ] Globales Exception Handling (`@ControllerAdvice`) statt generischer 500er-Antworten
- [ ] `DresdenCalculator`/`EloBerehcnung` liefern aktuell `void` statt der aktualisierten Spieler-Ratings zurück
- [ ] Bug in `addVerlierer()`: setzt aktuell den Gewinner statt eines eigenen Verlierer-Felds (das `MatchUp`-Model hat noch kein Verlierer-Feld)
- [ ] DB-Passwort aus `application.properties` in eine Umgebungsvariable/Secret auslagern
- [ ] Vereinsverwaltung (`Person`/`Mitglied`/`SchachVerein`) über eigene Endpunkte anbinden
- [ ] `pom.xml`-Metadaten (name, description, license, developer) ausfüllen
