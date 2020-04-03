package org.http4k.typesafe.routing.requests.paths.parsing

import com.natpryce.Result
import org.http4k.typesafe.routing.requests.paths.Match
import org.http4k.typesafe.routing.requests.paths.NoMatch
import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.map
import org.http4k.typesafe.routing.requests.paths.matchFailure
import org.http4k.typesafe.routing.requests.paths.matchSuccess

fun Path<String>.int() = this.map(
    mapper({ it: String -> it.toInt() }, "Expected a valid integer string"),
    { it.toString() })