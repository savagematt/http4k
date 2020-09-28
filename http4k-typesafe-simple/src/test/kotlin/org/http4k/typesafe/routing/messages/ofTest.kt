package org.http4k.typesafe.routing.messages

import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.typesafe.routing.at
import org.http4k.typesafe.routing.and
import org.http4k.typesafe.routing.request
import org.http4k.typesafe.routing.messages.body.textPlain
import org.junit.jupiter.api.Test

internal class OfTest {
    @Test
    fun `can get and set value`() {
        val ignoreUnitLens: IgnoreUnitLens<Request, String> = POST at "/" and request.text()

        requestContract(
            ignoreUnitLens,
            "hello world",
            expectedRequest = Request(POST, "/").textPlain("hello world"))
    }
}