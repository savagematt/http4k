package org.http4k.typesafe.routing.messages

import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.typesafe.routing.Simple.request
import org.http4k.typesafe.routing.SimpleRequestRouting.method
import org.http4k.typesafe.routing.fix
import org.http4k.typesafe.routing.messages.body.textPlain
import org.junit.jupiter.api.Test

internal class ButTest {
    @Test
    fun `can get and set value`() {
        val pairLens = method(POST, request.text())

        requestContract(
            pairLens.fix(),
            "hello world",
            Request(POST, "/").textPlain("hello world"))
    }
}