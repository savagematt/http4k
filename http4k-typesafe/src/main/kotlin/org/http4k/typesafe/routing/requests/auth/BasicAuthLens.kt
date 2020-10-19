package org.http4k.typesafe.routing.requests.auth

import com.natpryce.Result
import com.natpryce.Success
import org.http4k.base64Decoded
import org.http4k.base64Encode
import org.http4k.core.Credentials
import org.http4k.core.Request
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.http4k.typesafe.routing.messages.SimpleLens

class BasicAuthServerLens(val validator: (Credentials) -> Result<String, RoutingError>) : SimpleLens<Request, String> {
    override fun get(from: Request) =
        from.basicAuthenticationCredentials()?.let(validator)
            ?: routeFailed("Valid Authorization header required", UNAUTHORIZED)

    override fun set(into: Request, value: String) =
        throw UnsupportedOperationException("${this::class} should only be used server side")

}

class BasicAuthClientLens(val provider: (String) -> Credentials?) : SimpleLens<Request, String> {
    override fun get(from: Request) =
        throw UnsupportedOperationException("${this::class} should only be used client side")

    override fun set(into: Request, value: String) =
        provider(value)?.let { credentials ->
            Success(into.replaceHeader("Authorization", "Basic ${credentials.base64Encoded()}"))
        } ?: throw IllegalStateException("No credentials found for user '$value'")
}

fun Credentials.base64Encoded(): String = "$user:$password".base64Encode()

fun Request.basicAuthenticationCredentials(): Credentials? =
    header("Authorization")
        ?.trim()
        ?.takeIf { it.startsWith("Basic") }
        ?.substringAfter("Basic")
        ?.trim()
        ?.toCredentials()

fun String.toCredentials(): Credentials? =
    base64Decoded()
        .split(":")
        .let { Credentials(it.getOrElse(0) { "" }, it.getOrElse(1) { "" }) }
