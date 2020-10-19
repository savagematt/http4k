package org.http4k.typesafe.openapi.messages

import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.routing.with
import org.http4k.typesafe.openapi.routing.at
import org.http4k.typesafe.openapi.routing.request
import org.http4k.typesafe.routing.messages.body.textPlain
import org.junit.jupiter.api.Test

internal class OfTest {
    @Test
    fun `can get and set value`() {
        val ignoreUnitLens: OpenApiLens<Request, String> = POST at "/" with request.text()

        requestContract(
            ignoreUnitLens,
            "hello world",
            expectedRequest = Request(POST, "/").textPlain("hello world"))
    }
}