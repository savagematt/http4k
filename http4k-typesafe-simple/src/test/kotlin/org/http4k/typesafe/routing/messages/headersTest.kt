package org.http4k.typesafe.routing.messages

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.http4k.typesafe.routing.response
import org.http4k.typesafe.routing.response.required
import org.junit.jupiter.api.Test

class HeadersReplaceLensTest {
    @Test
    fun `can get and set`() {
        responseContract(
            response.headers("Set-Cookie"),
            listOf("a=123", "b=456"),
            // This Set-Cookie header should be replaced
            Response(OK).header("Set-Cookie", "should=be-deleted"),
            Response(OK).headers(listOf("Set-Cookie" to "a=123", "Set-Cookie" to "b=456"))
        )
    }

    @Test
    fun `can get and set null`() {
        responseContract(
            response.headers("Set-Cookie"),
            null,
            // This Set-Cookie header should be replaced
            Response(OK).header("Set-Cookie", "should=be-deleted"),
            Response(OK)
        )
    }

    @Test
    fun `can be set to required`() {
        val header = response.headers("Set-Cookie").required()

        assertThat(
            header.get(Response(OK)),
            equalTo<Result<List<String?>?, RoutingError>>(
                routeFailed(BAD_REQUEST, "Header 'Set-Cookie' is required"))
        )

        assertThat(
            header.set(Response(OK), listOf("a=123", "b=456")),
            equalTo<Result<Response, RoutingError>>(
                Success(Response(OK).headers(listOf("Set-Cookie" to "a=123", "Set-Cookie" to "b=456"))))
        )
    }
}