package org.http4k.typesafe.routing.messages

import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.typesafe.routing.messages.body.text
import org.http4k.typesafe.routing.messages.body.textPlain
import org.http4k.typesafe.routing.requests.method
import org.junit.jupiter.api.Test

internal class ButTest {
    @Test
    fun `can get and set value`() {
        val pairLens = method(POST) but text()

        requestContract(
            pairLens,
            "hello world",
            Request(POST, "/").textPlain("hello world"))
    }
}