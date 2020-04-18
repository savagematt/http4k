package org.http4k.openapi

import org.http4k.format.JsonLibAutoMarshallingJson
import org.http4k.util.Documentable

fun api(routes: List<Documentable<OpenApiRouteInfo>>): OpenApiObject =
    routes.fold(OpenApiObject.empty) { api, route ->
        route.document(OpenApiRouteInfo(api, OpenApiOperationInfo.empty)).let { (api, route) ->
            api.copy(paths = api.paths + route)
        }
    }

/**
 * Render openapi docs to a json string
 */
fun <NODE:Any> render(
    renderer: V3Renderer<NODE>,
    json: JsonLibAutoMarshallingJson<NODE>,
    routes: List<Documentable<OpenApiRouteInfo>>): String =

    json.pretty(
        renderer.render(
            api(routes)))
