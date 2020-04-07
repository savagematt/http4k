package org.http4k.typesafe.openapi

import com.natpryce.Result
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.openapi.routing.OpenApiRequestRouting.header
import org.http4k.typesafe.openapi.routing.OpenApiRequestRouting.required
import org.http4k.typesafe.openapi.ParameterLocation.HEADER
import org.http4k.typesafe.openapi.routing.document
import org.http4k.typesafe.openapi.routing.get
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.junit.jupiter.api.Test

class HeadersTest {
    @Test
    fun `docs work`() {
        val lens = header("X-My-Header")

        assertThat(
            lens.document().route.operation.parameters,
            equalTo<List<Referenceable<OpenApiParameter>>>(
                listOf(OpenApiParameter(
                    HEADER,
                    "X-My-Header"
                ).real()))
        )
    }

    @Test
    fun `required() docs are ok`() {
        val lens = header("X-My-Header").required()

        assertThat(
            lens.document().route.operation.parameters,
            equalTo<List<Referenceable<OpenApiParameter>>>(
                listOf(OpenApiParameter(
                    HEADER,
                    "X-My-Header",
                    required = true
                ).real()))
        )
    }

    @Test
    fun `required() routing message is ok`() {
        val lens: Kind2<ForOpenApiLens, Request, String> = header("X-My-Header").required()

        assertThat(
            lens.get(Request(GET, "/")),
            equalTo<Result<String, RoutingError>>(
                routeFailed(BAD_REQUEST, "Header 'X-My-Header' is required"))
        )
    }
}