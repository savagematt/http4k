package org.http4k.typesafe.routing.messages

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.Simple.request
import org.junit.jupiter.api.Test

class HeaderTest {
    private val header = request.header("Content-Type")

    @Test
    fun `can set`() {
        val actual = header.set(Request(GET, "/"), "text/plain")
        assertThat(
            actual,
            equalTo<Result<Request, RoutingError>>(
                Success(Request(GET, "/").header("Content-Type", "text/plain")))
        )

    }

    @Test
    fun `can set to null`() {
        val actual = header.set(
            Request(GET, "/")
                .header("Content-Type", "text/plain"), null)

        assertThat(
            actual,
            equalTo<Result<Request, RoutingError>>(
                Success(Request(GET, "/")))
        )

    }

    @Test
    fun `can get value`() {
        val header = request.header("Content-Type")

        val actual = header.get(Request(GET, "/")
            .header("Content-Type", "text/plain"))

        assertThat(
            actual,
            equalTo<Result<String?, RoutingError>>(
                Success("text/plain"))
        )

    }

    @Test
    fun `can get null`() {
        val header = request.header("Content-Type")

        val actual = header.get(Request(GET, "/"))

        assertThat(
            actual,
            equalTo<Result<String?, RoutingError>>(
                Success(null))
        )

    }

}