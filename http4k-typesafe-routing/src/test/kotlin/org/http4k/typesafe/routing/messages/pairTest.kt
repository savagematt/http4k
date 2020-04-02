package org.http4k.typesafe.routing.messages

import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.typesafe.routing.messages.body.text
import org.http4k.typesafe.routing.messages.body.textPlain
import org.http4k.typesafe.routing.requests.captureMethod
import org.junit.jupiter.api.Test


internal class PairTest {
    @Test
    fun `can get and set value`() {
        val pairLens = captureMethod() and text()

        requestContract(
            pairLens,
            POST to "hello world",
            Request(POST, "/").textPlain("hello world"))

    }
}