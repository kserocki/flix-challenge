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
| `apiKey` | `e8d85577-2fb3-4d44-8091-ffb3da46e9ce` (a public constant from the brief, kept in `BuildConfig`/data config, not in UI code) |

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
- `status.deviation` is sometimes present: `deviation_class` (`LATE`, `ON_TIME`, …), `deviation_seconds`, `deviation_timestamp` (estimated time). Sometimes it is `null`. **Not used now** (out of scope). It's a good candidate for a follow-up.
- `scheduled_timestamp` has no milliseconds, while the request format has them. The parser must accept both.
- `platform` is `null` at Berlin ZOB.
- The response returned rides already in scheduled order. We still sort (TT-5), because the API doesn't guarantee it.

## Errors to expect
| Situation | Mapped to |
|---|---|
| No network / DNS / timeout | `DomainError.Network` |
| HTTP 4xx/5xx | `DomainError.Server(code)` |
| Malformed JSON, missing `station`, invalid time zone | `DomainError.InvalidData` |
