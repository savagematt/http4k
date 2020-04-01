package org.http4k.typesafe.routing.messages

import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.typesafe.routing.messages.body.text
import org.http4k.typesafe.routing.requests.method
import org.junit.jupiter.api.Test

internal class ButLensTest {
    @Test
    fun `can get and set value`() {
        val pairLens = method(POST) but text()

        lensContract(
            pairLens,
            "hello world",
            Request(POST, "/")
                .header("Content-Type", TEXT_PLAIN.toHeaderValue())
                .body("hello world"))
    }
}