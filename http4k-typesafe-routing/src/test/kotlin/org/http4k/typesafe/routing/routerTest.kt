package org.http4k.typesafe.routing

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.*
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.typesafe.routing.messages.body.text
import org.http4k.typesafe.routing.messages.but
import org.http4k.typesafe.routing.requests.method
import org.junit.jupiter.api.Test

class ServerBehaviour() {
    val get: (Unit) -> String = { "hello world" }

    val post: (String) -> String = {
        "hello $it"
    }
}

object Routes {
    val get = Route(
        method(GET),
        text())

    val post = Route(
        method(POST) but text(),
        text())
}

@Suppress("MemberVisibilityCanBePrivate")
class RouterTest {

    val behaviour = ServerBehaviour()

    val getText = ServerRoute(Routes.get, behaviour.get)
    val postText = ServerRoute(Routes.post, behaviour.post)

    val router = router(listOf(getText, postText))

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
