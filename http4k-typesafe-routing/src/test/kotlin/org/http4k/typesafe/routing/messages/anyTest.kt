package org.http4k.typesafe.routing.messages

import org.http4k.core.HttpMessage
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.routing.Simple.any
import org.http4k.typesafe.routing.fix
import org.junit.jupiter.api.Test

internal class AnyTest {
    @Test
    fun `does nothing to messages and returns unit`() {
        lensContract(
            any<HttpMessage>().fix(),
            Unit,
            Request(GET, "/"),
            Response(OK))
    }

}