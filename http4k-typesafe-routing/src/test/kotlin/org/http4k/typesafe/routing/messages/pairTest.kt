package org.http4k.typesafe.routing.messages

import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.typesafe.routing.Simple.and
import org.http4k.typesafe.routing.Simple.method
import org.http4k.typesafe.routing.Simple.text
import org.http4k.typesafe.routing.fix
import org.http4k.typesafe.routing.messages.body.textPlain
import org.junit.jupiter.api.Test


internal class PairTest {
    @Test
    fun `can get and set value`() {
        val pairLens = method() and text()

        requestContract(
            pairLens.fix(),
            POST to "hello world",
            Request(POST, "/").textPlain("hello world"))

    }
}