package org.http4k.typesafe.routing.messages

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.http4k.typesafe.routing.request
import org.http4k.typesafe.routing.request.required
import org.http4k.typesafe.routing.response
import org.junit.jupiter.api.Test

class HeaderAppendTest {
    @Test
    fun `can get and set`() {
        requestContract(
            request.appendHeader("Content-Type"),
            "text/plain",
            // This Content-Type header should be retained
            Request(GET, "/").header("Content-Type", "application/json"),

            Request(GET, "/").headers(listOf("Content-Type" to "application/json", "Content-Type" to "text/plain")),
            "application/json"
        )
        responseContract(
            response.appendHeader("Content-Type"),
            "text/plain",
            // This Content-Type header should be replaced
            Response(OK).header("Content-Type", "application/json"),

            Response(OK).headers(listOf("Content-Type" to "application/json", "Content-Type" to "text/plain")),
            "application/json"

        )
    }

    @Test
    fun `can be set to required`() {
        val header = request.appendHeader("Content-Type").required()

        val actual = header.get(Request(GET, "/"))
        assertThat(
            actual,
            equalTo<Result<String?, RoutingError>>(
                routeFailed(BAD_REQUEST, "Header 'Content-Type' is required"))
        )

        assertThat(
            header.set(Request(GET, "/"), "text/plain"),
            equalTo<Result<Request, RoutingError>>(
                Success(Request(GET, "/").header("Content-Type", "text/plain")))
        )
    }
}