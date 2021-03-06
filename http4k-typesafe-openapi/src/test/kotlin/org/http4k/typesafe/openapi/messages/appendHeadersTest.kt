package org.http4k.typesafe.openapi.messages

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.openapi.routing.required
import org.http4k.typesafe.openapi.routing.response
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.junit.jupiter.api.Test

class HeadersAppendLensTest {
    @Test
    fun `can get and set`() {
        responseContract(
            response.appendHeaders("Set-Cookie"),
            listOf("a=123", "b=456"),
            // This Set-Cookie header should be retained
            Response(OK).header("Set-Cookie", "should=be-retained"),

            Response(OK).headers(listOf(
                "Set-Cookie" to "should=be-retained",
                "Set-Cookie" to "a=123",
                "Set-Cookie" to "b=456")),
            listOf("should=be-retained", "a=123", "b=456")
        )
    }

    @Test
    fun `can get and set null`() {
        responseContract(
            response.appendHeaders("Set-Cookie"),
            null,
            Response(OK).header("Set-Cookie", "should=be-retained"),
            // This Set-Cookie header should be retained
            Response(OK).headers(listOf("Set-Cookie" to "should=be-retained")),
            listOf("should=be-retained")
        )
    }

    @Test
    fun `can be set to required`() {
        val header = response.appendHeaders("Set-Cookie").required()

        assertThat(
            header.get(Response(OK)),
            equalTo<Result<List<String?>?, RoutingError>>(
                routeFailed("Header 'Set-Cookie' is required", BAD_REQUEST))
        )

        assertThat(
            header.set(Response(OK), listOf("a=123", "b=456")),
            equalTo<Result<Response, RoutingError>>(
                Success(Response(OK).headers(listOf("Set-Cookie" to "a=123", "Set-Cookie" to "b=456"))))
        )
    }
}