package org.http4k.typesafe.routing.requests.paths.parsing

import com.natpryce.Result
import org.http4k.typesafe.routing.requests.paths.Match
import org.http4k.typesafe.routing.requests.paths.NoMatch
import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.map
import org.http4k.typesafe.routing.requests.paths.matchFailure
import org.http4k.typesafe.routing.requests.paths.matchSuccess

fun Path<String>.double() = this.map(
    mapper(
        { it: String -> it.toDouble() },
        "Expected a valid double string"),
    { it.toString() })