package org.http4k.openapi

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.http4k.format.ConfigurableJackson
import org.http4k.format.Json
import org.http4k.format.asConfigurable
import org.http4k.format.customise
import org.http4k.util.Documentable
import org.http4k.util.JsonRenderer

val json: ConfigurableJackson = ConfigurableJackson(KotlinModule().asConfigurable().customise())

val renderer = V3Renderer(json,
    object : JsonRenderer<Any, JsonNode>, Json<JsonNode> by json {
        override fun render(value: Any): JsonNode =
            json.asJsonObject(value)
    })

/**
 * Renders openapi doc for routes, using Jackson
 */
fun render(routes: List<Documentable<OpenApiRouteInfo>>): String =
    render(
        renderer,
        json,
        routes
    )

fun render(concept: OpenApiConcept): String =
    json.pretty(renderer.render(concept))