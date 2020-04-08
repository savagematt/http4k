package org.http4k.typesafe.routing.requests.auth

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.HeaderReplaceLens
import org.http4k.typesafe.routing.messages.RequiredLens
import org.http4k.typesafe.routing.messages.SimpleLens
import kotlin.text.RegexOption.IGNORE_CASE

/**
 * Hide raw value in toString, because this is sensitive data
 * we do not want accidentally exposed in logs
 */
data class Jwt(val raw: String, val claims: Map<String, Any>) : Map<String, Any> by claims {
    override fun toString() = claims.toString()
}

interface JwtVerifier {
    fun verify(value: String): Result<Jwt, RoutingError>
}

class GetJwtLens<M : HttpMessage>(private val verifier: JwtVerifier,
                                  headerName: String = "Authorization",
                                  prefix: String = "Bearer")
    : SimpleLens<M, Jwt> {

    private val header: MessageLens<M, String> = RequiredLens(HeaderReplaceLens<M>(headerName))
    private val prefix = Regex("^" + Regex.escape(prefix) + "\\s*", IGNORE_CASE)

    override fun get(from: M) =
        header.get(from)
            .map { it.replace(prefix, "") }
            .flatMap(verifier::verify)

    override fun set(into: M, value: Jwt) =
        throw UnsupportedOperationException(
            "${this::class.simpleName} cannot set jwt. Are you using a client-side lens on the server?")
}

class SetJwtLens<M : HttpMessage> : SimpleLens<M, Jwt> {
    override fun get(from: M) =
        throw UnsupportedOperationException(
            "${this::class.simpleName} cannot get a jwt. Are you using a client-side lens in the server?")

    override fun set(into: M, value: Jwt) =
        into.addAuthorisation("Bearer ${value.raw}")
}

@Suppress("UNCHECKED_CAST")
private fun <M : HttpMessage> M.addAuthorisation(value: String): Result<M, RoutingError> =
    Success(this.replaceHeader("Authorization",
        listOfNotNull(
            this.header("Authorization"),
            value)
            .joinToString(", ")) as M)

