package org.http4k.openapi

/**
 * Used by lenses to add or make modifications to OpenApiRoute, but also
 * to idempotently add components like the security schemes and object
 * schemas used in this route to the top-level OpenApi.
 *
 * So a single lens can for example both add a security method into
 * components/securitySchemes and add that scheme to a route
 */
data class OpenApiRouteDocs(
    val api: OpenApiObject,
    val operation: OpenApiOperationInfo) {
    constructor(route: OpenApiOperationInfo) : this(OpenApiObject.empty, route)
    constructor(api: OpenApiObject) : this(api, OpenApiOperationInfo.empty)

    companion object {
        val empty = OpenApiRouteDocs(OpenApiObject.empty, OpenApiOperationInfo.empty)
    }
}

/**
 * This is here in case we change our mind about the type of OpenApiRouteInfo
 */
fun info(api: OpenApiObject, route: OpenApiOperationInfo) = OpenApiRouteDocs(api, route)