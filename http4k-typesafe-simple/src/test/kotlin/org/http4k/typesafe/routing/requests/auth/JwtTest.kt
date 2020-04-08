package org.http4k.typesafe.routing.requests.auth

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.HS256
import io.jsonwebtoken.SignatureAlgorithm.RS256
import io.jsonwebtoken.security.Keys
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.routing.Router
import org.http4k.typesafe.routing.RoutingErrorException
import org.http4k.typesafe.routing.Simple.but
import org.http4k.typesafe.routing.Simple.client
import org.http4k.typesafe.routing.Simple.response
import org.http4k.typesafe.routing.Simple.route
import org.http4k.typesafe.routing.Simple.server
import org.http4k.typesafe.routing.SimpleRequestRouting.bind
import org.http4k.typesafe.routing.SimpleResponseRouting.with
import org.http4k.typesafe.routing.SimpleServerRoute
import org.http4k.typesafe.routing.messages.SimpleLens
import org.junit.jupiter.api.Test
import java.security.Key

@Suppress("MemberVisibilityCanBePrivate")
class JwtTest() {
    private class TestRoutes(issueJwt: SimpleLens<Response, Jwt>, checkJwt: SimpleLens<Request, Jwt>) {
        companion object {
            fun server(public: Key) = TestRoutes(SetJwtLens(), public.lens())
            fun client(public: Key?) = TestRoutes(public?.lens() ?: IgnoreJwsSignature.lens(), SetJwtLens())
        }

        /**
         * In practise, this route might live on a different server, which issues
         * jwts using a private key, and then the route that needs to check the
         * token would use the public key to do so.
         */
        val issue = route(
            GET bind "/",
            OK with issueJwt
        )
        val check = route(
            POST bind "/" but checkJwt,
            OK with response.text()
        )
    }

    interface Api {
        val issue: (Unit) -> Jwt
        val check: (Jwt) -> String
    }

    @Test
    fun `works with symmetric key`() {
        val key = Keys.secretKeyFor(HS256)

        val jwtIssuedByServer = key.signJwt(claims)

        val server = server(key, jwtIssuedByServer)

        val client = client(server)

        val jwt = client.issue(Unit)

        assertThat(jwt, equalTo(jwtIssuedByServer))

        assertThat(client.check(jwt), equalTo("value from claim"))

        val issuedWithWrongKey = Keys.secretKeyFor(HS256).signJwt(claims)

        assertThat(
            { client.check(issuedWithWrongKey) },
            throws<RoutingErrorException>())
    }

    @Test
    fun `works with asymmetric key`() {
        val keys = Keys.keyPairFor(RS256)

        val jwtIssuedByServer = keys.private.signJwt(claims)

        val server = server(keys.public, jwtIssuedByServer)

        val client = client(server, keys.public)

        val jwt = client.issue(Unit)

        assertThat(jwt, equalTo(jwtIssuedByServer))

        assertThat(client.check(jwt), equalTo("value from claim"))

        val issuedWithWrongKey = Keys.keyPairFor(RS256).private.signJwt(claims)

        assertThat(
            { client.check(issuedWithWrongKey) },
            throws<RoutingErrorException>())
    }

    val claims = mapOf("some-claim" to "value from claim")

    private fun server(checkJwtUsing: Key, jwtToIssue: Jwt): Router<SimpleServerRoute<out Any, out Any>> {
        val routes = TestRoutes.server(checkJwtUsing)
        return Router(listOf(
            routes.issue server { jwtToIssue },
            routes.check server { it["some-claim"] as String }
        ))
    }

    private fun client(server: Router<SimpleServerRoute<out Any, out Any>>, public: Key? = null): Api {
        val routes = TestRoutes.client(public)
        return object : Api {
            override val issue = routes.issue client server
            override val check = routes.check client server
        }
    }
}

fun Key.signJwt(claims: Map<String, Any>): Jwt =
    Jwt(Jwts.builder()
        .setClaims(claims)
        .signWith(this)
        .compact(),
        claims
    )

