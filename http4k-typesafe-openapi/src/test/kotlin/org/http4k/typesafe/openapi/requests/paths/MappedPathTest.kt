package org.http4k.typesafe.openapi.requests.paths

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.typesafe.openapi.routing.bigInteger
import org.http4k.typesafe.openapi.routing.pathVar
import org.http4k.typesafe.openapi.routing.div
import org.http4k.typesafe.openapi.routing.double
import org.http4k.typesafe.openapi.routing.uuid
import org.http4k.typesafe.routing.requests.paths.PathResult
import org.http4k.typesafe.routing.requests.paths.matchFailure
import org.http4k.typesafe.routing.requests.paths.matchSuccess
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.util.*

class MappedPathTest {
    @Test
    fun `can set path`() {
        val widgetId = pathVar("id")
            .uuid()

        val path = "widgets" / widgetId

        assertThat(
            path.set("/api", UUID.fromString("b9cd3a85-2c02-4433-8d25-8950f46ab729")),
            equalTo<String>("/api/widgets/b9cd3a85-2c02-4433-8d25-8950f46ab729")
        )
    }

    @Test
    fun `can get path`() {
        val widgetId = pathVar("id")
            .bigInteger()

        val path = "widgets" / widgetId

        assertThat(
            path.get("widgets/123456789012345678901234567890"),
            equalTo<PathResult<BigInteger>>(
                matchSuccess(
                    BigInteger("123456789012345678901234567890"), ""))
        )
    }

    @Test
    fun `reports failure instead of throwing exception`() {
        val widgetId = pathVar("id")
            .double()

        val path = "widgets" / widgetId

        assertThat(
            path.get("widgets/123456.789NOTADOUBLE"),
            equalTo<PathResult<Double>>(
                matchFailure("Expected a double"))
        )
    }
}