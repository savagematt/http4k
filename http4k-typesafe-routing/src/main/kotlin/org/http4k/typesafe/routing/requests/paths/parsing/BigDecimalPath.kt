package org.http4k.typesafe.routing.requests.paths.parsing

import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.map

fun Path<String>.bigDecimal() = this.map(
    mapper(
        { it: String -> it.toBigDecimal() },
        "Expected a valid decimal string"),
    { it.toString() })