package org.http4k.typesafe.routing.requests.paths.parsing

import com.natpryce.Result
import org.http4k.typesafe.routing.requests.paths.Match
import org.http4k.typesafe.routing.requests.paths.NoMatch
import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.map
import org.http4k.typesafe.routing.requests.paths.matchFailure
import org.http4k.typesafe.routing.requests.paths.matchSuccess
import java.math.BigDecimal
import java.math.BigInteger

fun Path<String>.bigInteger() = this.map(
    mapper(
        { it: String -> it.toBigInteger() },
        "Expected a valid integer string"),
    { it.toString() })