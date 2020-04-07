package org.http4k.typesafe.routing

import com.natpryce.Result
import com.natpryce.flatMap
import com.natpryce.map
import com.natpryce.recover
import org.http4k.core.HttpHandler
import org.http4k.core.HttpMessage
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.util.functional.Kind2
import org.http4k.typesafe.routing.messages.ButLens
import org.http4k.typesafe.routing.messages.OrLens
import org.http4k.typesafe.routing.messages.ForSimpleLens
import org.http4k.typesafe.routing.messages.MappedLens
import org.http4k.typesafe.routing.messages.ResultMessageLens
import org.http4k.typesafe.routing.messages.SimpleLens
import org.http4k.typesafe.routing.messages.fix
import org.http4k.typesafe.routing.messages.oneOf.OneOf2Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple2Lens
import org.http4k.typesafe.routing.requests.paths.ForSimplePath

/*
Routes
------------------------------------------------------------------
 */

/** @see [org.http4k.util.functional.Kind2] */
class ForSimpleRoute private constructor() {
    companion object
}

/** @see [org.http4k.util.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <In, Out> Kind2<ForSimpleRoute, In, Out>.fix() = this as SimpleRoute<In, Out>

data class SimpleRoute<In, Out>(
    override val request: SimpleLens<Request, In>,
    override val response: SimpleLens<Response, Out>) :
    Kind2<ForSimpleRoute, In, Out>,
    Route<In, Out, SimpleLens<Request, In>, SimpleLens<Response, Out>> {
}

/*
Server Route
------------------------------------------------------------------
 */

/** @see [org.http4k.util.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
class ForSimpleServerRoute private constructor() {
    companion object
}

/** @see [org.http4k.util.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <In, Out> Kind2<ForSimpleServerRoute, In, Out>.fix() = this as SimpleServerRoute<In, Out>

data class SimpleServerRoute<In, Out>(
    val route: SimpleRoute<In, Out>,
    val handler: (In) -> Out
) : ServerRoute, Kind2<ForSimpleServerRoute, In, Out> {

    override fun handle(request: Request): Result<Response, RoutingError> =
        // try to extract handler parameter from request
        route.request.get(request)
            // pass the parameter to the handler function
            .map(handler)
            // inject the handler result into an empty Response
            .flatMap { route.response.set(Response(Status.OK), it) }
}

object Simple : Routing<ForSimpleServerRoute, ForSimpleRoute, ForSimpleLens, ForSimplePath> {
    override fun <In, Out> route(request: Kind2<ForSimpleLens, Request, In>,
                                 response: Kind2<ForSimpleLens, Response, Out>) =
        SimpleRoute(request.fix(), response.fix())

    override fun <In, Out> Kind2<ForSimpleRoute, In, Out>.server(handler: (In) -> Out) =
        SimpleServerRoute(this.fix(), handler).fix()

    override fun <In, Out> Kind2<ForSimpleRoute, In, Out>.client(http: HttpHandler):
        (In) -> Out {
        val route = this@client.fix()
        return { param: In ->
            route.request.set(Request(Method.GET, "/"), param)
                .map(http)
                .flatMap { response -> route.response.get(response) }
                .recover { throw it.exception() }
        }
    }

    override fun <M : HttpMessage, A, B> Kind2<ForSimpleLens, M, A>.and(
        other: Kind2<ForSimpleLens, M, B>) =
        Tuple2Lens(this.fix(), other.fix())

    override fun <M : HttpMessage, A, B> Kind2<ForSimpleLens, M, A>.xor(other: Kind2<ForSimpleLens, M, B>) =
        OneOf2Lens(this.fix(), other.fix())

    override fun <M : HttpMessage, T> Kind2<ForSimpleLens, M, T>.or(other: Kind2<ForSimpleLens, M, T>) =
        OrLens(listOf(this.fix(), other.fix()))

    override fun <M : HttpMessage, A, B> Kind2<ForSimpleLens, M, A>.map(
        getter: (A) -> Result<B, RoutingError>,
        setter: (B) -> A) =
        MappedLens(this.fix(), getter, setter)

    override fun <M : HttpMessage, T> Kind2<ForSimpleLens, M, Unit>.but(
        other: Kind2<ForSimpleLens, M, T>) =
        ButLens(this.fix(), other.fix())

    override fun <M : HttpMessage, T, E> result(
        success: Kind2<ForSimpleLens, M, T>,
        failure: Kind2<ForSimpleLens, M, E>) =
        ResultMessageLens(success.fix(), failure.fix())

    override val path = SimplePaths
    override val request = SimpleRequestRouting
    override val response = SimpleResponseRouting
}
