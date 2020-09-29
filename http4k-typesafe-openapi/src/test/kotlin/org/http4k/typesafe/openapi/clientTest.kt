package org.http4k.typesafe.openapi

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.openapi.routing.client
import org.http4k.typesafe.openapi.routing.method
import org.http4k.typesafe.openapi.routing.request
import org.http4k.typesafe.openapi.routing.response
import org.http4k.typesafe.openapi.routing.with
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.RoutingErrorException
import org.junit.jupiter.api.Test

@Suppress("MemberVisibilityCanBePrivate")
class ClientTest {
    val get = Route(
        method(GET, request.text()),
        OK with response.text())

    val server = { request: Request ->
        when (request.bodyString()) {
            "error" -> Response(INTERNAL_SERVER_ERROR)
            else -> Response(OK).body(request.bodyString())
        }
    }

    val client = get client server

    @Test
    fun `round trip works`() {
        assertThat(
            client("echo"),
            equalTo("echo")
        )
    }

    @Test
    fun `throws exception if server returns something the lens does not expect`() {
        /**
         * The route expects a 200 response. The string "error" will
         * trigger the server to return 500
         */
        assertThat(
            { client("error") },
            throws<RoutingErrorException>()
        )
    }
}