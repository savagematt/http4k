package org.http4k.typesafe.openapi

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.typesafe.openapi.routing.pathVar
import org.http4k.typesafe.openapi.routing.div
import org.http4k.typesafe.openapi.routing.literal
import org.junit.jupiter.api.Test

class PathsTest {
    @Test
    fun `literal docs`() {
        val route = literal("/widgets")
            .document(OpenApiRouteDocs.empty)
            .operation

        assertThat(
            route.path,
            equalTo("/widgets")
        )
    }

    @Test
    fun `consume docs`() {
        val route = pathVar("var")
            .document(OpenApiRouteDocs.empty)
            .operation

        assertThat(
            route.path,
            equalTo("/{var}")
        )
    }

    @Test
    fun `joined paths render docs`() {
        val path = "widgets" / pathVar("widgetId") / "components" / pathVar("componentId")
        val route = path
            .document(OpenApiRouteDocs.empty)
            .operation

        assertThat(
            route.path,
            equalTo("/widgets/{widgetId}/components/{componentId}")
        )
    }

}