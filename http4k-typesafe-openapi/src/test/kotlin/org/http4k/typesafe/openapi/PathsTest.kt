package org.http4k.typesafe.openapi

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.typesafe.openapi.OpenApiPaths.consume
import org.http4k.typesafe.openapi.OpenApiPaths.div
import org.http4k.typesafe.openapi.OpenApiPaths.literal
import org.junit.jupiter.api.Test

class PathsTest {
    @Test
    fun `literal docs`() {
        val route = literal("/widgets")
            .document(OpenApiRouteInfo.empty)
            .route

        assertThat(
            route.path,
            equalTo("/widgets")
        )
    }

    @Test
    fun `consume docs`() {
        val route = consume("var")
            .document(OpenApiRouteInfo.empty)
            .route

        assertThat(
            route.path,
            equalTo("/{var}")
        )
    }

    @Test
    fun `joined paths render docs`() {
        val path = "widgets" / consume("widgetId") / "components" / consume("componentId")
        val route = path
            .document(OpenApiRouteInfo.empty)
            .route

        assertThat(
            route.path,
            equalTo("/widgets/{widgetId}/components/{componentId}")
        )
    }

}