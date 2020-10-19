package org.http4k.typesafe.openapi.messages

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.openapi.routing.request
import org.http4k.typesafe.openapi.routing.required
import org.http4k.typesafe.openapi.routing.response
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.junit.jupiter.api.Test

class HeaderReplaceTest {
    @Test
    fun `can get and set`() {
        requestContract(
            request.header("Content-Type"),
            "text/plain",
            Request(GET, "/").header("Content-Type", "application/json"),
            // This Content-Type header should be replaced
            Request(GET, "/").header("Content-Type", "text/plain")
        )
        responseContract(
            response.header("Content-Type"),
            "text/plain",
            Response(OK).header("Content-Type", "application/json"),
            // This Content-Type header should be replaced
            Response(OK).header("Content-Type", "text/plain")
        )
    }

    @Test
    fun `can get and set null`() {
        requestContract(
            request.header("Content-Type"),
            null,
            Request(GET, "/").header("Content-Type", "application/json"),
            // This Content-Type header should be replaced
            Request(GET, "/")
        )
        responseContract(
            response.header("Content-Type"),
            null,
            Response(OK).header("Content-Type", "application/json"),
            // This Content-Type header should be replaced
            Response(OK)
        )
    }

    @Test
    fun `can be set to required`() {
        val header = request.header("Content-Type").required()

        val actual = header.get(Request(GET, "/"))
        assertThat(
            actual,
            equalTo<Result<String?, RoutingError>>(
                routeFailed("Header 'Content-Type' is required", BAD_REQUEST))
        )

        assertThat(
            header.set(Request(GET, "/"), "text/plain"),
            equalTo<Result<Request, RoutingError>>(
                Success(Request(GET, "/").header("Content-Type", "text/plain")))
        )
    }
}