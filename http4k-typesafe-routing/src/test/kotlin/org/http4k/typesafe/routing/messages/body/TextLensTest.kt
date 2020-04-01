package org.http4k.typesafe.routing.messages.body

import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.routing.messages.lensContract
import org.junit.jupiter.api.Test

internal class TextLensTest {
    @Test
    fun `injects text and content-type header`() {
        lensContract(
            text(),
            "hello world",
            Request(GET, "/")
                .header("Content-Type", TEXT_PLAIN.toHeaderValue())
                .body("hello world"),

            Response(OK, "/")
                .header("Content-Type", TEXT_PLAIN.toHeaderValue())
                .body("hello world"))
    }

}