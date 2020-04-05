package org.http4k.typesafe.routing.requests

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.SimplePaths.consume
import org.http4k.typesafe.routing.SimplePaths.div
import org.http4k.typesafe.routing.SimplePaths.long
import org.http4k.typesafe.routing.SimpleRequestRouting.bind
import org.junit.jupiter.api.Test

class PathLensTest {
    @Test
    fun `extracts path`() {
        val id = consume("id").long()
        val lens: CheckMethodLens<Long> = GET bind "widgets" / id
        assertThat(
            lens.get(Request(GET, "widgets/12345")),
            equalTo<Result<Long, RoutingError>>(Success(12345L))
        )
    }
}