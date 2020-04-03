package org.http4k.typesafe.routing.messages.body

import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.routing.Simple.request
import org.http4k.typesafe.routing.Simple.response
import org.http4k.typesafe.routing.messages.requestContract
import org.http4k.typesafe.routing.messages.responseContract
import org.http4k.typesafe.routing.simple.fix
import org.junit.jupiter.api.Test


internal class TextLensTest {
    @Test
    fun `injects text and content-type header`() {
        requestContract(
            request.text().fix(),
            "hello world",
            Request(GET, "/")
                .header("Content-Type", TEXT_PLAIN.toHeaderValue())
                .body("hello world"))

        responseContract(
            response.text().fix(),
            "hello world",
            Response(OK, "/")
                .header("Content-Type", TEXT_PLAIN.toHeaderValue())
                .body("hello world"))

    }

}