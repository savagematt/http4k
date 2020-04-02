package org.http4k.typesafe.routing

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.*
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.Simple.but
import org.http4k.typesafe.routing.Simple.method
import org.http4k.typesafe.routing.Simple.route
import org.http4k.typesafe.routing.Simple.serve
import org.http4k.typesafe.routing.Simple.text
import org.junit.jupiter.api.Test

class ServerBehaviour() {
    val get: (Unit) -> String = { "hello world" }

    val post: (String) -> String = {
        "hello $it"
    }
}

object Routes {
    val get = route(
        method(GET),
        text())

    val post = route(
        method(POST) but text(),
        text())
}

@Suppress("MemberVisibilityCanBePrivate")
class RouterTest {

    val behaviour = ServerBehaviour()

    val getText: Kind2<ForSimpleServerRoute, Unit, String> = Routes.get serve behaviour.get
    val postText: Kind2<ForSimpleServerRoute, String, String> = Routes.post serve behaviour.post

    val router = Router(
        listOf(
            getText.fix(),
            postText.fix())
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
