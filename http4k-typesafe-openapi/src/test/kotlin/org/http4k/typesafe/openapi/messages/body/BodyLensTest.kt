package org.http4k.typesafe.openapi.messages.body

import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.openapi.messages.requestContract
import org.http4k.typesafe.openapi.messages.responseContract
import org.http4k.typesafe.openapi.routing.request
import org.http4k.typesafe.openapi.routing.response
import org.junit.jupiter.api.Test


internal class BodyLensTest {
    @Test
    fun `injects text and content-type header`() {
        requestContract(
            request.text(),
            "hello world",
            expectedRequest = Request(GET, "/")
                .header("Content-Type", TEXT_PLAIN.toHeaderValue())
                .body("hello world"))

        responseContract(
            response.text(),
            "hello world",
            expectedResponse = Response(OK, "/")
                .header("Content-Type", TEXT_PLAIN.toHeaderValue())
                .body("hello world"))

    }

}