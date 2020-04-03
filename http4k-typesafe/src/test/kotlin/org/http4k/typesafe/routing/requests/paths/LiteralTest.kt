package org.http4k.typesafe.routing.requests.paths

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo


import org.junit.jupiter.api.Test

class LiteralTest {
    @Test
    fun `matches path and returns unit`() {
        assertThat(
            Literal("widgets").get("widgets"),
            equalTo<PathResult<Unit>>(
                matchSuccess(
                    Unit, ""))
        )
    }

    @Test
    fun `ignores slashes in expected value`() {
        assertThat(
            Literal("/widgets/").get("widgets"),
            equalTo<PathResult<Unit>>(
                matchSuccess(
                    Unit, ""))
        )
    }

    @Test
    fun `ignores slashes in path value`() {
        assertThat(
            Literal("/widgets/").get("/widgets/"),
            equalTo<PathResult<Unit>>(
                matchSuccess(
                    Unit, "/"))
        )
    }

    @Test
    fun `matches whole segments only`() {
        assertThat(
            Literal("widgets").get("widgets-etc"),
            equalTo<PathResult<Unit>>(
                matchFailure(
                    "Path did not match whole contents up to /"))
        )
    }

    @Test
    fun `can consist of multiple segments`() {
        assertThat(
            Literal("widgets/etc").get("widgets/etc"),
            equalTo<PathResult<Unit>>(
                matchSuccess(
                    Unit, ""))
        )

        assertThat(
            Literal("widgets/etc").get("widgets/etc/etc"),
            equalTo<PathResult<Unit>>(
                matchSuccess(
                    Unit, "/etc"))
        )
    }
}