package org.http4k.typesafe.routing

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK

data class ServerRoute<In, Out>(val route: Route<In, Out>, val handler: (In) -> Out) {
    fun handle(request: Request): Result<Response, RoutingError> =
        // try to extract handler parameter from request
        route.request.get(request)
            // pass the parameter to the handler function
            .map(handler)
            // inject the handler result into an empty Response
            .flatMap { route.response.set(Response(OK), it) }
}

class Router(val routes: List<ServerRoute<*, *>>) : HttpHandler {
    fun route(request: Request): Response =
        routes
            // do this lazily so we only call routes until we reach a match
            .asSequence()
            // route tries to handle the request
            .map { route -> route.handle(request) }
            // we ignore routes that did not match
            .filterIsInstance<Success<Response>>()
            // then if no routes matched, we return not found
            .firstOrNull()
            ?.value ?: Response(NOT_FOUND)

    override operator fun invoke(request: Request): Response = route(request)
}

fun router(routes: List<ServerRoute<*, *>>) =
    Router(routes)