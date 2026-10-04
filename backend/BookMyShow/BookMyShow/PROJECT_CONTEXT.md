# BookMyMovie — Project Context

Full-stack movie ticket booking platform (BookMyShow clone). React frontend,
Spring Boot 3.5.4 + PostgreSQL + Elasticsearch backend. Schema is managed via
Hibernate ddl-auto=update, not migrations — there is no Flyway in this repo
despite earlier notes suggesting otherwise. Treat the JPA entity definitions
as the schema source of truth.
CQRS-style split: Postgres is the write/source-of-truth model, Elasticsearch
is the denormalized read model for discovery/search/filtering.

## Elasticsearch documents (read model)
- `Movie_Document` — city-agnostic, powers the movie details page
- `Movie_City_Document` — one per movie-per-city, powers listing/filtering;
  stores `availableFormats` directly — don't re-derive it via aggregation
- `Show_Document` — denormalized show data, includes `screenName` at index
  time (not fetched from Postgres at read time)

## Controller boundary
- `MovieController` = discovery only ("what and where")
- `ShowController` = transactional show data ("when, seats, pricing")
- Filters/facets have their own dedicated endpoints, not folded into `/movies`

## Seat holding (Phase 3 — built, concurrency-verified)
- `ShowSeat` = many-to-many join between `Show` and `Seat`, status enum
  `AVAILABLE/HELD/BOOKED/BLOCKED`
- Pricing: `Show` has `classicPrice/premiumPrice/reclinerPrice`; `ShowSeat.price`
  is a snapshot taken once at fan-out time, not recalculated later
- `SeatHold` = one row per seat sharing a `holdGroupId`; status enum
  `ACTIVE/EXPIRED/CONFIRMED/RELEASED` (released, never deleted — audit trail)
- Concurrency guarantee is a partial unique index:
  `UNIQUE(show_seat_id) WHERE hold_status = 'ACTIVE'`, backed by pessimistic
  locking + the 409-on-violation pattern — verified via JMeter (20 threads,
  1 hold created, 19 clean 409s)
- `format` lives on `Screen.screenType`, NOT on `Show` — a screen's
  projection tech doesn't vary per showtime. This was a corrected mistake;
  don't reintroduce a per-show format field.
- No real auth yet: seat holds use a browser-generated `sessionId` (UUID in
  localStorage) as a placeholder. Real auth is an explicit separate
  design task, not assumed to exist anywhere in the flow.

## Design principles (apply these before proposing changes)
- Simplicity over elegance at current scale — prefer Spring Data derived
  queries and Java-side processing over native queries/aggregation unless
  clearly justified
- Concurrency-sensitive operations need a DB constraint, not just an
  application-level lock (seat holds do this; show-overlap checks
  deliberately do NOT — overlap race risk was judged negligible at this scale)
- Pagination correctness: when merging two data sources, combine + sort
  first, then paginate — never paginate each source independently

## Current build state
- Done: homepage/discovery (`GET /movies`), seat holds backend (3 endpoints),
  `POST /admin/shows` with overlap check, movie details → show selection flow
- Known bug: "All Formats" filter on Book Tickets page returns zero results
  (specific formats work) — suspected null-format handling in the ES derived
  query, not yet fixed
- Not started: `Booking` entity/confirmation endpoint (Phase 4), show
  update/cancel endpoints, `AdminMovieController`, `AdminTheatreController`
  (screen seat-layout currently only exists as seed data — no endpoint),
  `SeatHold` expiry sweep job (code written, not confirmed wired in)

## Known gotchas
- Windows PowerShell: multi-line curl with `\` continuation silently drops
  the request body — use single-line curl or Postman
- `co.elastic.clients` 5.5.x: `StringTermsBucket::key` needs
  `.key().stringValue()`; range queries need `.date(d -> d...)`, not
  `JsonData.of("now")`
