package org.http4k.typesafe.routing

import com.natpryce.Result
import com.natpryce.Success
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND

interface ServerRoute {
    fun handle(request: Request): Result<Response, RoutingError>
}

class Router<Route : ServerRoute>(
    val routes: List<Route>) : HttpHandler {
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