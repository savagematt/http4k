package org.http4k.typesafe.routing

import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.flatMap
import com.natpryce.flatMapFailure
import com.natpryce.map
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR

interface ServerRoute<In, Out, D> {
    val route: Route<In, Out, D>
    val handler: (In) -> Out

    fun handle(request: Request) =
        route.request.get(request)
            // pass the parameter to the handler function
            .map(handler)
            // inject the handler result into an empty Response
            .flatMap { route.response.set(Response(Status.OK), it) }
}

interface RouteHandler<Docs> {
    /**
     * Start routing.
     *
     * [RouteHandler] may modify the request here, e.g. adding a timing header
     */
    fun begin(request: Request): Request

    /**
     * This route successfully handled the request.
     *
     * The [Router] will not consider any more routes, and will return [Response]
     */
    fun rightRoute(request: Request, route: ServerRoute<*, *, Docs>, response: Response): Response

    /**
     * This was the wrong route
     *
     * [Router] will continue looking for a matching route
     */
    fun wrongRoute(request: Request, route: ServerRoute<*, *, Docs>, error: RoutingError.WrongRoute)

    /**
     * This was the right route, but the request is not valid for this route (e.g. the body is not serialized correctly)
     *
     * The [Router] will not consider any more routes, and will return [Response]
     */
    fun routeFailed(request: Request, route: ServerRoute<*, *, Docs>, error: RoutingError.RouteFailed): Response

    /**
     * *NB: If this method ever throws an exception the router will print stack trace and return an empty 500*
     *
     * Either a route or another method in this [RouteHandler] threw an unexpected exception.
     *
     * [route] may be null if this method is called after [RouteHandler.notFound] throws an exception
     *
     * The [Router] will not consider any more routes, and will return [Response], which would usually be a 500
     */
    fun serverError(request: Request, route: ServerRoute<*, *, Docs>?, exception: Exception): Response

    /**
     * *NB: If this method ever throws an exception the router will print stack trace and return an empty 500*
     *
     * [Router] has tried every available route, and none matched the request
     *
     * [Router] will return [Response], presumably a 404
     *
     * A debug [RouteHandler] might set the [Response] body to be a list of each route tried and its
     * [RoutingError.WrongRoute] message.
     */
    fun notFound(request: Request): Response
}

class Router<Docs>(
    val handler: RouteHandler<Docs>,
    val routes: List<ServerRoute<*, *, Docs>>)
    : HttpHandler {

    constructor(handler: RouteHandler<Docs>, vararg routes: ServerRoute<*, *, Docs>) :
        this(handler, listOf(*routes))

    fun route(request: Request): Response =
        try {
            doRouting(handler.begin(request))
        } catch (e: Exception) {
            /**
             * Catastrophic exception in either:
             * - handler.notFound
             * - handler.serverException
             * TODO: what to do here? Do we have any better options?
             */
            e.printStackTrace()
            Response(INTERNAL_SERVER_ERROR)
        }

    private fun doRouting(request: Request): Response {
        return routes
            // do this lazily so we only call routes until we reach a match
            .asSequence()

            .map { route ->
                try {
                    tryToHandleRequest(route, request)
                } catch (e: Exception) {
                    // Either the route failed, or one of the methods on handler threw an exception
                    Success(handler.serverError(request, route, e))
                }
            }

            // Return the Response from the first correctly matched route.
            //
            // The Response may be from:
            // - The successful response of a matched route
            // - RoutingError.RouteFailed.response of a matched route
            // - handler.serverException
            .filterIsInstance<Success<Response>>()
            .firstOrNull()?.value

            // Then if no routes matched, we return not found
            ?: try {
                handler.notFound(request)
            } catch (e: Exception) {
                handler.serverError(request, null, e)
            }

    }

    private fun tryToHandleRequest(route: ServerRoute<*, *, Docs>, request: Request): Result<Response, RoutingError> {
        return route.handle(request)
            // Route matched- return Response
            .map { handler.rightRoute(request, route, it) }

            .flatMapFailure { failure ->
                when (failure) {

                    // Keep looking for routes
                    is RoutingError.WrongRoute -> {
                        handler.wrongRoute(request, route, failure)
                        Failure(failure)
                    }

                    // Stop looking for routes and return Response
                    is RoutingError.RouteFailed -> {
                        val response = handler.routeFailed(request, route, failure)
                        Success(response)
                    }
                }
            }
    }

    override operator fun invoke(request: Request): Response = route(request)
}