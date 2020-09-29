package org.http4k.typesafe.openapi

import com.natpryce.Result
import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.openapi.OpenApiRouteInfo
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.ServerRoute

typealias OpenApiRoute<In, Out> = Route<In, Out, OpenApiRouteInfo>

data class OpenApiServerRoute<In, Out>(
    val route: Route<In, Out, OpenApiRouteInfo>,
    val handler: (In) -> Out
) : ServerRoute {

    override fun handle(request: Request): Result<Response, RoutingError> =
        // try to extract handler parameter from request
        route.request.get(request)
            // pass the parameter to the handler function
            .map(handler)
            // inject the handler result into an empty Response
            .flatMap { route.response.set(Response(Status.OK), it) }
}
