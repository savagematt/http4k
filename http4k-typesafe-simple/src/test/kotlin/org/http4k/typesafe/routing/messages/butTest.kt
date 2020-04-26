package org.http4k.typesafe.routing.messages

import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.typesafe.routing.bind
import org.http4k.typesafe.routing.but
import org.http4k.typesafe.routing.request
import org.http4k.typesafe.routing.messages.body.textPlain
import org.junit.jupiter.api.Test

internal class ButTest {
    @Test
    fun `can get and set value`() {
        val butLens: ButLens<Request, String> = POST bind "/" but request.text()

        requestContract(
            butLens,
            "hello world",
            expectedRequest = Request(POST, "/").textPlain("hello world"))
    }
}