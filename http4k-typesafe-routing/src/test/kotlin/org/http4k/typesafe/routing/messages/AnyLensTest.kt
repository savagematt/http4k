package org.http4k.typesafe.routing.messages

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test

internal class AnyLensTest {
    @Test
    fun `does nothing to messages and returns unit`() {
        lensContract(any(),
            Unit,
            Request(GET, "/"),
            Response(OK))
    }

}