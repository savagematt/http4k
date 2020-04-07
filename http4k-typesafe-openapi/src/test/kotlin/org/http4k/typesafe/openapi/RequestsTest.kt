package org.http4k.typesafe.openapi

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.typesafe.openapi.routing.OpenApiPaths
import org.http4k.typesafe.openapi.routing.OpenApiPaths.div
import org.http4k.typesafe.openapi.routing.OpenApiRequestRouting.path
import org.http4k.typesafe.openapi.routing.document
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