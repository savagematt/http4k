package org.http4k.typesafe.routing.messages

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.routing.request
import org.http4k.typesafe.routing.response
import org.junit.jupiter.api.Test

internal class AnyTest {
    @Test
    fun `does nothing to messages and returns unit`() {
        requestContract(
            request.any(),
            Unit,
            Request(GET, "/"))

        responseContract(
            response.any(),
            Unit,
            Response(OK))
    }

}