# Flix timetable API

## Endpoint

```
GET https://global.api.flixbus.com/gis/v2/timetable/{stationId}/departures?from={from}&to={to}&apiKey={apiKey}
```

| Parameter | Value |
|---|---|
| `stationId` | `dcbaaee6-9603-11e6-9066-549f350fcb0c` (Berlin central bus station) |
| `from` | now, ISO-8601 UTC with ms, for example `2026-03-05T14:11:51.485Z` |
| `to` | now + 2h, same format |
| `apiKey` | `e8d85577-2fb3-4d44-8091-ffb3da46e9ce` (a public constant from the brief, kept in `core:data`'s internal `FlixApiConfig` next to the station id and base URL, so the multiplatform data layer is complete on every platform. It isn't a secret: it ships in the client either way) |

Check it by hand:

```bash
FROM=$(date -u +%Y-%m-%dT%H:%M:%S.000Z); TO=$(date -u -v+2H +%Y-%m-%dT%H:%M:%S.000Z)   # macOS date
curl -s "https://global.api.flixbus.com/gis/v2/timetable/dcbaaee6-9603-11e6-9066-549f350fcb0c/departures?from=$FROM&to=$TO&apiKey=e8d85577-2fb3-4d44-8091-ffb3da46e9ce" | jq '.station, (.rides | length)'
```

## Fields we use → domain

| JSON | Domain | Notes |
|---|---|---|
| `station.id` | `Station.id` | |
| `station.name` | `Station.name` | |
| `station.timezone` | `Station.timeZone` | IANA id. Invalid or missing → `InvalidData` (TT-9) |
| `rides[].id` | `Departure.id` | Dedup key (TT-6) |
| `rides[].line.code` | `Departure.lineCode` | String, can be numeric-looking (`"1322"`) |
| `rides[].line.direction` | `Departure.direction` | |
| `rides[].status.scheduled_timestamp` | `Departure.scheduledAt` | ISO-8601 UTC (`2026-11-16T16:00:00Z`, no ms) |

Everything else is ignored (`ignoreUnknownKeys = true`). DTOs declare only these fields, all of them nullable where a single bad ride shouldn't break the list (TT-16).

## Observed facts (real response, 2026-11-16, saved in `departures-response-sample.json`)
- Each ride has many more fields than the brief shows: `calls[]` (all stops of the ride, each with its own `timezone`), `location`, `vehicle`, `platform`, `omitted_calls`, `brand`.
- `status.deviation` is sometimes present: `deviation_class` (`LATE`, `ON_TIME`, …), `deviation_seconds`, `deviation_timestamp` (estimated time). Sometimes it is `null`. **Not used now** (out of scope). Tracked in #10.
- `scheduled_timestamp` has no milliseconds, while the request format has them. The parser must accept both.
- `platform` is `null` at Berlin ZOB.
- The response returned rides already in scheduled order. We still sort (TT-5), because the API doesn't guarantee it.
- (2026-11-17) Two **different** rides can look identical on the board: same `line.code`, `direction` and `scheduled_timestamp`, but different `id` and `trip_number`. Seen: `CBS221` to Bucharest at 16:00Z, partner brand `COMATI`. They're real rides, so both are shown. TT-6 dedups by `id` only, never by visible content.

## Errors to expect
| Situation | Mapped to |
|---|---|
| No network / DNS / timeout (any I/O error, incl. Darwin errors on iOS) | `DomainError.Network` |
| HTTP 4xx/5xx. Observed: 401 bad key, 400 missing params, 404 unknown station | `DomainError.Server(code)` |
| Malformed or non-JSON body, missing `station`, missing/`null` `rides`, invalid time zone (TT-9), every ride broken (TT-16a) | `DomainError.InvalidData` |
| Any other unexpected exception at the data boundary | `DomainError.InvalidData` (fallback; `CancellationException` is always rethrown) |
