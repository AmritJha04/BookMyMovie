# AGENTS.md

Single-module Spring Boot 3.5.4 / Java 21 Maven app (package `com.app.BookMyShow`).
REST API only — no frontend in this repo; a separate frontend is assumed to run at
`http://localhost:5173` (Vite default).

## Commands

- Compile: `./mvnw compile` (Windows: `mvnw.cmd compile`) — fastest verification.
- Test: `./mvnw test`. The only test is `@SpringBootTest contextLoads`, which boots
  the full app and needs a reachable Postgres per
  `src/main/resources/application.properties` (localhost:5432, database `BookMyShow`).
  Testcontainers is declared in `pom.xml` but not used by any test.
- No lint, formatter, typecheck, or CI config exists. Don't look for them;
  `compile` + `test` is the whole verification story.

## Runtime prerequisites

- Postgres is NOT in `docker-compose.yml`. It must already be running with the
  `BookMyShow` database created; connection config lives (with credentials) in
  `application.properties`.
- Elasticsearch: `docker compose up elasticsearch` (8.15, single-node, security
  disabled, port 9200).
- No migrations (no Flyway/Liquibase). `spring.jpa.hibernate.ddl-auto=update`
  applies schema changes from the JPA entities in `src/main/java/.../entity/` —
  entities are the schema source of truth.

## Architecture

- Layering: `controller` → `service` → `repository` (JPA) → Postgres.
  Controllers live in `controller/app` (user-facing), `controller/admin`, and
  `controller/AuthController`.
- Dual data path: Postgres is authoritative for bookings (shows, seats, holds);
  movie browse/search/filters/aggregations in `service/MovieService` read from
  Elasticsearch indexes under the `search/` package.
- ES indexes are populated ONLY by `POST /adminreindex/reindex`
  (`controller/admin/ReindexController` → `search/service/ReindexService`),
  which rebuilds documents from Postgres. After seeding/changing data or document
  mappings, call reindex or search results will be stale/empty.
- Seat holds: pessimistic row locks (`ShowSeatRepository.lockForHold`) plus a DB
  unique-index backstop; hold TTL = `seat-hold.duration-seconds` (default 300,
  not set in application.properties).

## Conventions & gotchas

- Every controller carries `@CrossOrigin(origins = "http://localhost:5173",
  allowCredentials = "true")`. New controllers need it too or the frontend gets
  CORS errors.
- Controllers mix `@Controller` and `@RestController`; both rely on returning
  `ResponseEntity` (no `@ResponseBody`).
- Error handling convention: broad `catch (Exception)` returning an empty 400,
  with diagnostics via `System.out.println` instead of a Logger.
- `/auth/login` is a stub that returns 200 without validating credentials;
  there is no Spring Security. Don't assume auth exists.
- Lombok everywhere; its annotation processor is explicitly configured in
  `pom.xml` — don't remove that config.
- Known typo: class `controller/app/ShowControlller` (three l's). Search for it
  as-is; renaming is a deliberate change, not a drive-by fix.
