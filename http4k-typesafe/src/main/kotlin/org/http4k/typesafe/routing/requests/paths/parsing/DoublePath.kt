package org.http4k.typesafe.routing.requests.paths.parsing

import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.map

fun Path<String>.double() = this.map(
    mapper(
        { it: String -> it.toDouble() },
        "Expected a valid double string"),
    { it.toString() })