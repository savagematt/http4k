package org.http4k.typesafe.routing

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND

/**
 * Does not leak stack traces
 *
 * // TODO:
 */
open class ProductionRouteHandler<D> : RouteHandler<D> {
    override fun begin(request: Request): Request =
        request

    override fun rightRoute(request: Request, route: ServerRoute<*, *, D>, response: Response) =
        response

    override fun wrongRoute(request: Request, route: ServerRoute<*, *, D>, error: RoutingError.WrongRoute) {
        // No-op
    }

    override fun routeFailed(request: Request, route: ServerRoute<*, *, D>, error: RoutingError.RouteFailed): Response =
        error.response

    override fun serverError(request: Request, route: ServerRoute<*, *, D>?, exception: Exception): Response =
        Response(INTERNAL_SERVER_ERROR)

    override fun notFound(request: Request) =
        Response(NOT_FOUND)

}