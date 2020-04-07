package org.http4k.typesafe.openapi

import com.natpryce.Result
import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.openapi.OpenApiRouteInfo
import org.http4k.util.functional.Kind2
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.ServerRoute
import org.http4k.util.Documentable

/** @see [org.http4k.util.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
class ForOpenApiServerRoute private constructor() {
    companion object
}

/** @see [org.http4k.util.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <In, Out> Kind2<ForOpenApiServerRoute, In, Out>.fix() = this as OpenApiServerRoute<In, Out>

data class OpenApiServerRoute<In, Out>(
    val route: OpenApiRoute<In, Out>,
    val handler: (In) -> Out
) : ServerRoute, Kind2<ForOpenApiServerRoute, In, Out> {

    override fun handle(request: Request): Result<Response, RoutingError> =
        // try to extract handler parameter from request
        route.request.get(request)
            // pass the parameter to the handler function
            .map(handler)
            // inject the handler result into an empty Response
            .flatMap { route.response.set(Response(Status.OK), it) }
}

/** @see [org.http4k.util.functional.Kind2] */
class ForOpenApiRoute private constructor() {
    companion object
}

/** @see [org.http4k.util.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <In, Out> Kind2<ForOpenApiRoute, In, Out>.fix() = this as OpenApiRoute<In, Out>

data class OpenApiRoute<In, Out>(
    override val request: OpenApiLens<Request, In>,
    override val response: OpenApiLens<Response, Out>,
    val extraDocs: (OpenApiRouteInfo) -> OpenApiRouteInfo = { it })
    : Kind2<ForOpenApiRoute, In, Out>,
    Route<In, Out, OpenApiLens<Request, In>, OpenApiLens<Response, Out>>,
    Documentable<OpenApiRouteInfo> {

    override fun document(doc: OpenApiRouteInfo): OpenApiRouteInfo =
        extraDocs(response.document(request.document(doc)))
}

/**
 *
 */
@Suppress("unused")
fun <In, Out> OpenApiRoute<In, Out>.doc(extraDocs: (OpenApiRouteInfo) -> OpenApiRouteInfo) =
    OpenApiRoute(this.request, this.response, extraDocs)