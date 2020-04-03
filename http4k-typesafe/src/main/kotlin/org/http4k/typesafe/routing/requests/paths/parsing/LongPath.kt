package org.http4k.typesafe.routing.requests.paths.parsing

import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.map

fun Path<String>.toLong() = this.map(
    mapper({ it: String -> it.toLong() }, "Expected a valid long string"),
    { it.toString() })