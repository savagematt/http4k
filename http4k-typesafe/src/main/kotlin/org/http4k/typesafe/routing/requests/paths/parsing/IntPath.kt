package org.http4k.typesafe.routing.requests.paths.parsing

import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.map

fun Path<String>.int() = this.map(
    mapper({ it: String -> it.toInt() }, "Expected a valid integer string"),
    { it.toString() })