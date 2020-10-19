package org.http4k.typesafe.openapi.routing

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.RouteHandler
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.ServerRoute
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Convenience class for building [RouteHandler]s by implementing a single [StatefulRouteHandler.handle] method
 * containing all the state for the request
 *
 * Maintains a list of [RoutingFailure]s for each request as routes fail to match, and then when the request eventually
 * succeeds or fails, calls [StatefulRouteHandler.handle] with:
 *
 * - The [Request]
 * - A list of [RoutingError.WrongRoute]s for each route that did not match the request
 * - The [Route] that eventually matched, if any
 * - A [RoutingError.RouteFailed], if the request did not succeed
 * - A [Response], which implementing classes may modify (for example to add stack traces)
 */
abstract class StatefulRouteHandler : RouteHandler<OpenApiRouteDocs> {
    private val DEBUG_REQUEST_ID = "X-DebugRouteHandler"
    private val unmatched = ConcurrentHashMap<String, List<RoutingFailure>>()

    private fun failures(id: String) =
        this.unmatched.getOrDefault(id, emptyList())

    override fun begin(request: Request): Request =
        request.header(DEBUG_REQUEST_ID, UUID.randomUUID().toString())

    override fun wrongRoute(request: Request, route: ServerRoute<*, *, OpenApiRouteDocs>, error: RoutingError.WrongRoute) {
        val id = request.header(DEBUG_REQUEST_ID)!!
        unmatched[id] = this.unmatched.getOrDefault(id, emptyList()) + RoutingFailure(route.route, error)
    }

    override fun rightRoute(request: Request, route: ServerRoute<*, *, OpenApiRouteDocs>, response: Response) =
        complete(
            request,
            route.route,
            null,
            response
        )

    override fun routeFailed(request: Request, route: ServerRoute<*, *, OpenApiRouteDocs>, error: RoutingError.RouteFailed): Response =
        complete(
            request,
            route.route,
            error,
            error.response)

    override fun serverError(request: Request, route: ServerRoute<*, *, OpenApiRouteDocs>?, exception: Exception): Response {
        val response = Response(INTERNAL_SERVER_ERROR)
        return complete(
            request,
            route?.route,
            RoutingError.RouteFailed("Server error", exception, response),
            response)
    }

    override fun notFound(request: Request): Response {
        return complete(
            request,
            null,
            null,
            Response(Status.NOT_FOUND))
    }

    @Synchronized
    private fun complete(
        request: Request,
        matched: Route<*, *, OpenApiRouteDocs>?,
        error: RoutingError.RouteFailed?,
        response: Response
    ): Response {
        val id = request.header(DEBUG_REQUEST_ID)!!
        val failures = failures(id)
        this.unmatched.remove(id)

        return handle(
            request.removeHeader(DEBUG_REQUEST_ID),
            failures,
            matched,
            error,
            response
        )
    }

    protected abstract fun handle(
        request: Request,
        unmatched: List<RoutingFailure>,
        matched: Route<*, *, OpenApiRouteDocs>?,
        error: RoutingError.RouteFailed?,
        response: Response
    ): Response
}