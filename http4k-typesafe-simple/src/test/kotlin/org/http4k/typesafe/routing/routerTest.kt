package org.http4k.typesafe.routing

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.typesafe.routing.request
import org.http4k.typesafe.routing.response
import org.http4k.typesafe.routing.route
import org.http4k.typesafe.routing.server
import org.http4k.typesafe.routing.method
import org.junit.jupiter.api.Test

class ServerBehaviour() {
    val get: (Unit) -> String = { "hello world" }

    val post: (String) -> String = {
        "hello $it"
    }
}

object Routes {
    val get: SimpleRoute<Unit, String> = route(
        method(GET, request.any()),
        response.text())

    val post: SimpleRoute<String, String> = route(
        method(POST, request.text()),
        response.text())
}

@Suppress("MemberVisibilityCanBePrivate")
class RouterTest {

    val behaviour = ServerBehaviour()

    val getText: SimpleServerRoute< Unit, String> = Routes.get server behaviour.get
    val postText: SimpleServerRoute< String, String> = Routes.post server behaviour.post

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
