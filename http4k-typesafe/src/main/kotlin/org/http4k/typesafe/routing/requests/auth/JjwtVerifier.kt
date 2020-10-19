package org.http4k.typesafe.routing.requests.auth

import com.natpryce.Result
import com.natpryce.Success
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SigningKeyResolver
import org.http4k.core.Response
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.http4k.typesafe.routing.Try
import java.security.Key

internal class SingleKeyResolver(val key: Key) : SigningKeyResolver {
    override fun resolveSigningKey(header: JwsHeader<out JwsHeader<*>>?, claims: Claims?): Key =
        key

    override fun resolveSigningKey(header: JwsHeader<out JwsHeader<*>>?, plaintext: String?): Key =
        key
}

class JjwtVerifier(resolver: SigningKeyResolver, val trier: Try) : JwtVerifier {
    constructor(key: Key, trier: Try) : this(SingleKeyResolver(key), trier)

    private val parser = Jwts.parserBuilder().setSigningKeyResolver(resolver).build()
    override fun verify(value: String): Result<Jwt, RoutingError> =
        trier(
            {
                val claims = parser.parseClaimsJws(value).body
                Success(Jwt(value, claims))
            },
            { routeFailed("Invalid jwt", it, Response(UNAUTHORIZED)) })
}

/**
 * DANGEROUS!
 *
 * This MUST NOT be used as a verifier server-side, because it
 * does no verification.
 *
 * This takes a jws (which is a signed jwt), then parses the
 * payload without verifying the signature was correct.
 *
 * This is useful client-side to parse a jws that has been
 * issued by a server using a symmetric signing key.
 *
 * In this case, there is no public key, and obviously only
 * the server has the private key. So the client must just
 * trust that the server has signed the key correctly and
 * go ahead and parse the payload regardless.
 *
 * The Jwt will retain the raw jws string, so it can be used
 * later to authenticate with the server.
 */
object NoVerification : JwtVerifier {
    private val parser = Jwts.parserBuilder().build()
    override fun verify(value: String): Result<Jwt, RoutingError> =
        try {
            val stripSignature = value.substring(0, value.lastIndexOf('.') + 1)
            val claims = parser.parseClaimsJwt(stripSignature).body
            Success(Jwt(value, claims))
        } catch (e: Exception) {
            routeFailed("Invalid jwt", e, Response(UNAUTHORIZED))
        }
}

fun Key.signJwt(claims: Map<String, Any>): Jwt =
    Jwt(Jwts.builder()
        .setClaims(claims)
        .signWith(this)
        .compact(),
        claims
    )
