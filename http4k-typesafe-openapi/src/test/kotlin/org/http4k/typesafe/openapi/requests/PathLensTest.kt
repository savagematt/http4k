package org.http4k.typesafe.openapi.requests

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.routing.at
import org.http4k.typesafe.openapi.routing.consume
import org.http4k.typesafe.openapi.routing.div
import org.http4k.typesafe.openapi.routing.long
import org.http4k.typesafe.routing.RoutingError
import org.junit.jupiter.api.Test

class PathLensTest {
    @Test
    fun `extracts path`() {
        val id = consume("id").long()
        val lens: OpenApiLens<Request, Long> = GET at "widgets" / id
        assertThat(
            lens.get(Request(GET, "widgets/12345")),
            equalTo<Result<Long, RoutingError>>(Success(12345L))
        )
    }
}