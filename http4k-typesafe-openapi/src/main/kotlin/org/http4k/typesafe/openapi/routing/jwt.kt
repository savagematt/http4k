package org.http4k.typesafe.openapi.routing

import org.http4k.core.Response
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.openapi
import org.http4k.typesafe.routing.requests.auth.Jwt
import org.http4k.typesafe.routing.requests.auth.JwtLens
import org.http4k.typesafe.routing.requests.auth.JwtVerifier
import org.http4k.typesafe.routing.requests.auth.NoVerification

/**
 * Use [NoVerification] client side to embed a provided jwt into a request
 */
fun jwt(verifier: JwtVerifier,
        headerName: String = "Authorization",
        prefix: String = "Bearer") =
    JwtLens(verifier, headerName, prefix) openapi {} // TODO: jwt openapi docs

fun jwtAsBody(): OpenApiLens<Response, Jwt> {
    return response.text().map(
        { jwt: String -> NoVerification.verify(jwt) },
        { jwt: Jwt -> jwt.raw }
    )
}
