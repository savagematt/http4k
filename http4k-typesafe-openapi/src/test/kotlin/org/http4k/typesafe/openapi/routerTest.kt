package org.http4k.typesafe.openapi

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.typesafe.openapi.routing.method
import org.http4k.typesafe.openapi.routing.request
import org.http4k.typesafe.openapi.routing.response
import org.http4k.typesafe.openapi.routing.server
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.Router
import org.junit.jupiter.api.Test

class ServerBehaviour() {
    val get: (Unit) -> String = { "hello world" }

    val post: (String) -> String = {
        "hello $it"
    }
}

private object RouterTestRoutes {
    val get: OpenApiRoute<Unit, String> = Route(method(GET, request.any()),
        response.text())

    val post: OpenApiRoute<String, String> = Route(method(POST, request.text()),
        response.text())
}

@Suppress("MemberVisibilityCanBePrivate")
class RouterTest {

    val behaviour = ServerBehaviour()

    val getText: OpenApiServerRoute<Unit, String> = RouterTestRoutes.get server behaviour.get
    val postText: OpenApiServerRoute<String, String> = RouterTestRoutes.post server behaviour.post

    val router = Router(
        listOf(
            getText,
            postText)
    )

    @Test
    fun `returns result of the matching route`() {
        assertThat(
            router.invoke(Request(GET, "/")).bodyString(),
            equalTo("hello world"))

        assertThat(
            router.invoke(Request(POST, "/").body("dave")).bodyString(),
            equalTo("hello dave"))
    }

    @Test
    fun `returns 404 if no routes match`() {
        val deleteRequest = Request(DELETE, "/")

        assertThat(
            router.invoke(deleteRequest),
            equalTo(Response(NOT_FOUND)))
    }
}
