package org.http4k.typesafe.routing.requests.paths.parsing

import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.map

fun Path<String>.bigInteger() = this.map(
    mapper(
        { it: String -> it.toBigInteger() },
        "Expected a valid integer string"),
    { it.toString() })