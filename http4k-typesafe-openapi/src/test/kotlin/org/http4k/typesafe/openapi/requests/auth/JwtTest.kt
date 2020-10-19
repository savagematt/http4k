package org.http4k.typesafe.openapi.requests.auth

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import io.jsonwebtoken.SignatureAlgorithm.HS256
import io.jsonwebtoken.SignatureAlgorithm.RS256
import io.jsonwebtoken.security.Keys
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.typesafe.openapi.OpenApiRoute
import org.http4k.typesafe.openapi.routing.DebugRouteHandler
import org.http4k.typesafe.openapi.routing.at
import org.http4k.typesafe.openapi.routing.client
import org.http4k.typesafe.openapi.routing.jwt
import org.http4k.typesafe.openapi.routing.jwtAsBody
import org.http4k.typesafe.openapi.routing.response
import org.http4k.typesafe.openapi.routing.server
import org.http4k.typesafe.openapi.routing.with
import org.http4k.typesafe.routing.PrintStackTrace
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.Router
import org.http4k.typesafe.routing.RoutingErrorException
import org.http4k.typesafe.routing.Try
import org.http4k.typesafe.routing.requests.auth.JjwtVerifier
import org.http4k.typesafe.routing.requests.auth.Jwt
import org.http4k.typesafe.routing.requests.auth.JwtVerifier
import org.http4k.typesafe.routing.requests.auth.NoVerification
import org.http4k.typesafe.routing.requests.auth.signJwt
import org.junit.jupiter.api.Test
import java.security.Key


private class TestRoutes(verifier: JwtVerifier) {

    companion object {
        fun server(verifyWith: Key, trier: Try = PrintStackTrace) = TestRoutes(JjwtVerifier(verifyWith, trier))

        fun client() = TestRoutes(NoVerification)
    }

    /**
     * In practise, this route might live on a different server, which issues
     * jwts using a private key, and then the route that needs to check the
     * token would use the public key to do so.
     */
    val issue: OpenApiRoute<Unit, Jwt> = Route(
        GET at "/",
        OK with jwtAsBody()
    )

    val check: OpenApiRoute<Jwt, String> = Route(
        POST at "/" with jwt(verifier),
        OK with response.text()
    )
}

interface Api {
    val issue: (Unit) -> Jwt
    val check: (Jwt) -> String
}

@Suppress("MemberVisibilityCanBePrivate")
class JwtTest {

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

        val client = client(server)

        val jwt = client.issue(Unit)

        assertThat(jwt, equalTo(jwtIssuedByServer))

        assertThat(client.check(jwt), equalTo("value from claim"))

        val issuedWithWrongKey = Keys.keyPairFor(RS256).private.signJwt(claims)

        assertThat(
            { client.check(issuedWithWrongKey) },
            throws<RoutingErrorException>())
    }

    val claims = mapOf("some-claim" to "value from claim")

    private fun server(checkJwtUsing: Key, jwtToIssue: Jwt): Router<OpenApiRouteDocs> {
        val routes = TestRoutes.server(checkJwtUsing)

        return Router(
            DebugRouteHandler(),
            routes.issue server { jwtToIssue },
            routes.check server { it["some-claim"] as String }
        )
    }

    private fun client(server: HttpHandler): Api {
        val routes = TestRoutes.client()
        return object : Api {
            override val issue = routes.issue client server
            override val check = routes.check client server
        }
    }
}


