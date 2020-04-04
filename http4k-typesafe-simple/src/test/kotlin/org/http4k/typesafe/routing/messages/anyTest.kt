package org.http4k.typesafe.routing.messages

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.routing.Simple.request
import org.http4k.typesafe.routing.Simple.response
import org.junit.jupiter.api.Test

internal class AnyTest {
    @Test
    fun `does nothing to messages and returns unit`() {
        requestContract(
            request.any().fix(),
            Unit,
            Request(GET, "/"))

        responseContract(
            response.any().fix(),
            Unit,
            Response(OK))
    }

}