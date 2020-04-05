package org.http4k.typesafe.openapi

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.typesafe.openapi.OpenApiPaths.div
import org.http4k.typesafe.openapi.OpenApiRequestRouting.path
import org.junit.jupiter.api.Test

class RequestsTest {
    @Test
    fun `path docs`() {
        val p = "widgets" / OpenApiPaths.consume("widgetId") / "components" / OpenApiPaths.consume("componentId")

        val route = path(p)
            .document()
            .route

        assertThat(
            route.path,
            equalTo("/widgets/{widgetId}/components/{componentId}")
        )
    }

}