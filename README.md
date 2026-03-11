Végleges, értékelésre szánt release: v2.1-teacher-fixes

A legfrissebb stabil állapot és letölthető forrás az oldalsávon a Releases résznél található.

Transport Planner (Webuni Záróvizsga) – Teacher spec solution

Ez a projekt a Webuni záróvizsga feladat (Transport Planner) tanári specifikációja alapján készült Spring Boot alkalmazás.
A megoldás lefedi az értékelt részeket: Address CRUD + dinamikus keresés/lapozás, TransportPlan delay (időeltolás + bevételcsökkentés konfigból), integrációs tesztek, valamint JWT alapú Security.

Fő funkciók (pontozási részek)

Entitások és kapcsolatok: Address, Milestone, Section, TransportPlan

Address CRUD (spec szerinti státuszkódokkal)

POST: 200 OK, ha requestben van id → 400

PUT: id mismatch (path vs body) → 400

DELETE: mindig 200 OK, akkor is, ha nem létező id

Address keresés: POST /api/addresses/search

Body alapú filter + lapozás

X-Total-Count header

Üres/blank filter body → 400

size nélkül: “összes találat”

TransportPlan delay

milestone/section alapú időeltolás

várható bevétel csökkentése konfigurációból (küszöbök alapján)

Integrációs tesztek

Delay IT + validációs/security tesztek (JWT-vel)

Security (JWT)

POST /api/login → token (10 perc)

Jogosultságok authority-k alapján (ADDRESS_MANAGER, TRANSPORT_MANAGER)

Tech stack

Java + Spring Boot

Spring Web, Spring Data JPA (Hibernate)

H2 in-memory DB (dev/test)

Spring Security + JWT (com.auth0 java-jwt)

JUnit 5 + WebTestClient (integration tests)

Indítás (lokál)

Build / tesztek:

mvn test

App indítás:

mvn spring-boot:run
Alapértelmezett: http://localhost:8080

Konfiguráció (application.properties)

H2:

spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1

JWT:

transport.jwt.secret=...

transport.jwt.durationSeconds=600

Security – bejelentkezés és jogosultságok
Default felhasználók (in-memory)

addressmgr / pass123 → ADDRESS_MANAGER

transportmgr / pass123 → TRANSPORT_MANAGER

admin / pass123 → mindkettő

Token kérés

POST http://localhost:8080/api/login
Body:
{ "username": "transportmgr", "password": "pass123" }

Válasz:
{ "token": "..." }

Védett végpontok

Header:
Authorization: Bearer <token>

API quickstart (Postman minták)
Address létrehozás (ADDRESS_MANAGER tokennel)

POST /api/addresses
Body:
{
"country": "HU",
"zip": "1041",
"city": "Budapest",
"street": "Fo utca",
"houseNumber": "1",
"latitude": 47.567,
"longitude": 19.12
}

Address search

POST /api/addresses/search
Body:
{ "country": "HU" }
Headerben: X-Total-Count

Delay (TRANSPORT_MANAGER tokennel)

POST /api/transportplans/{id}/delay
Body:
{ "milestoneId": 1, "minutes": 30 }

Postman collection

postman/transport-planner.postman_collection.json

H2 Console (DB ellenőrzés)

URL: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1

User: sa

Password: (üres)

Hasznos SQL-ek:

SHOW TABLES;

FK-k:
SELECT CONSTRAINT_NAME, FK_TABLE_NAME, FK_COLUMN_NAME, PK_TABLE_NAME, PK_COLUMN_NAME FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE FK_TABLE_SCHEMA='PUBLIC' ORDER BY FK_TABLE_NAME, CONSTRAINT_NAME;

Release / Tag

v2.1-teacher-fixes (release/tag)

Készítette: Pál Gábor (Gabesz79)
