package org.http4k.typesafe.openapi

import org.http4k.format.JsonLibAutoMarshallingJson
import org.http4k.openapi.OpenApiObject
import org.http4k.openapi.OpenApiOperationInfo
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.openapi.V3Renderer

fun api(routes: List<OpenApiRoute<*, *>>): OpenApiObject =
    routes.fold(OpenApiObject.empty) { api, route ->
        // response.document(request.document(doc))
        route.response.document(route.request.document(OpenApiRouteDocs(api, OpenApiOperationInfo.empty)))
            .let { (api, route) ->
                api.copy(paths = api.paths + route)
            }
    }

/**
 * Render openapi docs to a json string
 */
fun <NODE : Any> render(
    renderer: V3Renderer<NODE>,
    json: JsonLibAutoMarshallingJson<NODE>,
    routes: List<OpenApiRoute<*, *>>): String =
    json.pretty(
        renderer.render(
            api(routes)))