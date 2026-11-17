package com.kserocki.flix.core.data.fixture

/**
 * Verbatim from `docs/domain/departures-response-sample.json`: rides `[0, 4, 8]`, each with
 * `calls` trimmed to its first 2 entries (`jq '{station, rides: [.rides[0,4,8] | .calls |= .[0:2]]}'`),
 * so unknown fields (`calls`, `location`, `vehicle`, `deviation`, nested stop `timezone`s) are
 * still exercised (TT-17). `excludeRawStrings` keeps detekt's line-length rule off this literal.
 */
internal val REAL_DEPARTURES_RESPONSE = """
{"station":{"id":"dcbaaee6-9603-11e6-9066-549f350fcb0c","name":"Berlin central bus station","timezone":"Europe/Berlin","description":"Gate information available here: https://zob.berlin/en","coordinates":{"latitude":52.507171,"longitude":13.279399}},"rides":[{"id":"96e32f9f-ba2b-4690-affb-12a3caebc06d","status":{"segment":null,"next_stop_sequence":null,"has_arrived_at_next_stop":false,"progress":null,"deviation":{"deviation_timestamp":"2026-11-16T16:29:39Z","deviation_seconds":1779,"reason":null,"deviation_class":"LATE","deviation_type":"ESTIMATED","updated_at":"2026-11-16T15:56:57Z"},"scheduled_timestamp":"2026-11-16T16:00:00Z"},"platform":null,"line":{"code":"1322","direction":"Flensburg central train station","name":"Warsaw - Berlin - Flensburg","trip_number":77,"means_of_transport":"BUS","brand":{"id":"a18f138c-68fa-4b45-a42f-adb0378e10d3","name":"FlixBus"}},"location":{"coordinates":{"latitude":52.470535,"longitude":13.370822},"updated_at":"2026-11-16T16:17:58Z","speed_category":"MODERATE"},"calls":[{"sequence":1,"stop":{"id":"dcc454ac-9603-11e6-9066-549f350fcb0c","type":"BUS","timezone":"Europe/Warsaw","name":"Warsaw, Bus Station West","description":"FlixBus lines: check information about platform on Your ticket or station screens.\nInternational trips, which also include domestic part, will only depart from the international platforms (without approaching domestic platforms).\n\nŻAK lines to Ełk, Białystok - platform 3.","city":{"id":"40e19c59-8646-11e6-9066-549f350fcb0c","name":"Warsaw"},"code":"WZA","location":{"latitude":52.21809187569917,"longitude":20.963779243751247}},"arrival":null,"departure":{"scheduled":"2026-11-16T07:50:00Z","deviation":null,"platform":null}},{"sequence":2,"stop":{"id":"dcc3dad0-9603-11e6-9066-549f350fcb0c","type":"BUS","timezone":"Europe/Warsaw","name":"Poznan, Bus Station","description":"Please pay attention to station announcements - platforms are different depending on the direction!\nDeparture platforms: 3, 5-8, 11-16.\nArrival platforms - 17-19.","city":{"id":"40de8b67-8646-11e6-9066-549f350fcb0c","name":"Poznań"},"code":"POZ","location":{"latitude":52.402757,"longitude":16.913948}},"arrival":{"scheduled":"2026-11-16T11:25:00Z","deviation":null,"platform":null},"departure":{"scheduled":"2026-11-16T11:40:00Z","deviation":null,"platform":null}}],"omitted_calls":null,"vehicle":{"id":"336e990e-43c2-40dd-be1e-22278e35f7b4","license_plate":null,"type":"BUS"},"brand":null},{"id":"3277af6c-c109-40f0-8c57-57af98761198","status":{"segment":null,"next_stop_sequence":null,"has_arrived_at_next_stop":null,"progress":null,"deviation":null,"scheduled_timestamp":"2026-11-16T16:30:00Z"},"platform":null,"line":{"code":"050","direction":"Hamburg central bus station","name":"Hamburg - Berlin","trip_number":3,"means_of_transport":"BUS","brand":{"id":"a18f138c-68fa-4b45-a42f-adb0378e10d3","name":"FlixBus"}},"location":{"coordinates":{"latitude":52.50732,"longitude":13.279533},"updated_at":"2026-11-16T16:19:05Z","speed_category":"STATIONARY"},"calls":[{"sequence":1,"stop":{"id":"dcbaaee6-9603-11e6-9066-549f350fcb0c","type":"BUS","timezone":"Europe/Berlin","name":"Berlin central bus station","description":"Gate information available here: https://zob.berlin/en","city":{"id":"40d8f682-8646-11e6-9066-549f350fcb0c","name":"Berlin"},"code":"B","location":{"latitude":52.507171,"longitude":13.279399}},"arrival":null,"departure":{"scheduled":"2026-11-16T16:30:00Z","deviation":null,"platform":null}},{"sequence":2,"stop":{"id":"dcbfd6bc-9603-11e6-9066-549f350fcb0c","type":"BUS","timezone":"Europe/Berlin","name":"Berlin Alt-Tegel (metro station)","description":"","city":{"id":"40d8f682-8646-11e6-9066-549f350fcb0c","name":"Berlin"},"code":"BAT","location":{"latitude":52.591713,"longitude":13.282017}},"arrival":{"scheduled":"2026-11-16T16:40:00Z","deviation":null,"platform":null},"departure":{"scheduled":"2026-11-16T16:40:00Z","deviation":null,"platform":null}}],"omitted_calls":null,"vehicle":{"id":"2e73c691-73e2-4961-8557-f8cebe45ed00","license_plate":null,"type":"BUS"},"brand":null},{"id":"846fad51-24a3-4ae2-9ac1-e93d8a370a62","status":{"segment":null,"next_stop_sequence":null,"has_arrived_at_next_stop":false,"progress":null,"deviation":{"deviation_timestamp":"2026-11-16T18:05:00Z","deviation_seconds":0,"reason":null,"deviation_class":"ON_TIME","deviation_type":"ESTIMATED","updated_at":"2026-11-15T14:30:39Z"},"scheduled_timestamp":"2026-11-16T18:05:00Z"},"platform":null,"line":{"code":"1926","direction":"Hamburg central bus station","name":"Bucharest - Berlin","trip_number":1,"means_of_transport":"BUS","brand":{"id":"a18f138c-68fa-4b45-a42f-adb0378e10d3","name":"FlixBus"}},"location":{"coordinates":{"latitude":50.821926,"longitude":13.88852},"updated_at":"2026-11-16T15:21:39Z","speed_category":"STATIONARY"},"calls":[{"sequence":1,"stop":{"id":"54c7a569-1af8-4252-aa35-eba0ff50b89b","type":"BUS","timezone":"Europe/Bucharest","name":"Bucharest (bus station Militari)","description":"","city":{"id":"40e110b4-8646-11e6-9066-549f350fcb0c","name":"Bucharest"},"code":"BM","location":{"latitude":44.43294003940949,"longitude":26.00627256679535}},"arrival":null,"departure":{"scheduled":"2026-11-15T14:30:00Z","deviation":null,"platform":null}},{"sequence":2,"stop":{"id":"75760c8d-df72-4a4f-abaf-7b8e935e309e","type":"BUS","timezone":"Europe/Bucharest","name":"Râmnicu Vâlcea (Autogara Leximpol)","description":"","city":{"id":"e448e4bc-2484-472b-bb07-ccec00b312cf","name":"Râmnicu Vâlcea"},"code":"RAVA","location":{"latitude":45.11148,"longitude":24.37828}},"arrival":{"scheduled":"2026-11-15T17:05:00Z","deviation":null,"platform":null},"departure":{"scheduled":"2026-11-15T17:10:00Z","deviation":null,"platform":null}}],"omitted_calls":null,"vehicle":{"id":"852b7f78-185c-46a8-9d9c-3a0fd9d6378e","license_plate":null,"type":"BUS"},"brand":null}]}

""".trimIndent()

private const val BERLIN_STATION_JSON =
    "{\"id\":\"dcbaaee6-9603-11e6-9066-549f350fcb0c\",\"name\":\"Berlin central bus station\",\"timezone\":\"Europe/Berlin\"}"

/** Builds a departures response body, leaving out `station`/`rides` entirely when `null`. */
internal fun departuresResponseJson(
    station: String? = BERLIN_STATION_JSON,
    rides: List<String>? = emptyList(),
): String {
    val fields = buildList {
        station?.let { add("\"station\":$it") }
        rides?.let { add("\"rides\":[${it.joinToString(",")}]") }
    }
    return "{${fields.joinToString(",")}}"
}

/** Builds one ride, leaving out each key whose argument is `null` (TT-16). */
internal fun rideJson(
    id: String? = "96e32f9f-ba2b-4690-affb-12a3caebc06d",
    code: String? = "1322",
    direction: String? = "Flensburg central train station",
    scheduledTimestamp: String? = "2026-11-16T16:00:00Z",
): String {
    val lineFields = buildList {
        code?.let { add("\"code\":\"$it\"") }
        direction?.let { add("\"direction\":\"$it\"") }
    }
    val statusFields = buildList {
        scheduledTimestamp?.let { add("\"scheduled_timestamp\":\"$it\"") }
    }
    val fields = buildList {
        id?.let { add("\"id\":\"$it\"") }
        if (lineFields.isNotEmpty()) add("\"line\":{${lineFields.joinToString(",")}}")
        if (statusFields.isNotEmpty()) add("\"status\":{${statusFields.joinToString(",")}}")
    }
    return "{${fields.joinToString(",")}}"
}
