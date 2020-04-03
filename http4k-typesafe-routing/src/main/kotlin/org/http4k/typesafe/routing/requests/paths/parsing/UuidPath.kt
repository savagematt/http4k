package org.http4k.typesafe.routing.requests.paths.parsing

import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.map
import java.util.*

fun Path<String>.uuid() = this.map(
    mapper(UUID::fromString, "Expected a valid UUID string"),
    { it.toString() })