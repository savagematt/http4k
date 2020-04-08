package org.http4k.typesafe.routing.messages

import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.util.data.tuple
import org.http4k.typesafe.routing.and
import org.http4k.typesafe.routing.request
import org.http4k.typesafe.routing.messages.body.textPlain
import org.http4k.typesafe.routing.method
import org.junit.jupiter.api.Test


internal class PairTest {
    @Test
    fun `can get and set value`() {
        val pairLens = method() and request.text()

        requestContract(
            pairLens,
            tuple(POST, "hello world"),
            Request(POST, "/").textPlain("hello world"))

    }
}