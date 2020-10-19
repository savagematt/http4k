package org.http4k.typesafe.routing.requests.auth

import com.natpryce.Result
import org.http4k.typesafe.routing.RoutingError

interface JwtVerifier {
    fun verify(value: String): Result<Jwt, RoutingError>
}