package org.http4k.typesafe.routing

import com.natpryce.Result
import com.natpryce.flatMap
import com.natpryce.map
import com.natpryce.recover
import org.http4k.core.*
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.messages.AnyLens
import org.http4k.typesafe.routing.messages.ButLens
import org.http4k.typesafe.routing.messages.PairLens
import org.http4k.typesafe.routing.messages.ResultMessageLens
import org.http4k.typesafe.routing.messages.body.TextLens
import org.http4k.typesafe.routing.requests.CheckMethodLens
import org.http4k.typesafe.routing.requests.MethodLens
import org.http4k.typesafe.routing.responses.CheckStatusLens
import org.http4k.typesafe.routing.responses.StatusLens

/*
Lenses
------------------------------------------------------------------
 */
/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
class ForSimpleLens private constructor() {
    companion object
}

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <M : HttpMessage, T> Kind2<ForSimpleLens, M, T>.fix() = this as SimpleLens<M, T>

interface SimpleLens<M : HttpMessage, T> : MessageLens<M, T>, Kind2<ForSimpleLens, M, T>


/*
Routes
------------------------------------------------------------------
 */

/** @see [org.http4k.typesafe.functional.Kind2] */
class ForSimpleRoute private constructor() {
    companion object
}

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <In, Out> Kind2<ForSimpleRoute, In, Out>.fix() = this as SimpleRoute<In, Out>

data class SimpleRoute<In, Out>(
    val request: SimpleLens<Request, In>,
    val response: SimpleLens<Response, Out>) : Kind2<ForSimpleRoute, In, Out>


/*
Server Route
------------------------------------------------------------------
 */

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
class ForSimpleServerRoute private constructor() {
    companion object
}

/** @see [org.http4k.typesafe.functional.Kind2]
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

/*
Grammar
------------------------------------------------------------------
 */

object Simple : Routing<ForSimpleServerRoute, ForSimpleRoute, ForSimpleLens> {
    override fun <In, Out> route(request: Kind2<ForSimpleLens, Request, In>,
                                 response: Kind2<ForSimpleLens, Response, Out>) =
        SimpleRoute(request.fix(), response.fix())

    override fun <In, Out> Kind2<ForSimpleRoute, In, Out>.serve(handler: (In) -> Out) =
        SimpleServerRoute(this.fix(), handler).fix()

    override fun <In, Out> Kind2<ForSimpleRoute, In, Out>.consume(http: HttpHandler):
        (In) -> Out {
        val route = this@consume.fix()
        return { param: In ->
            route.request.set(Request(Method.GET, "/"), param)
                .map(http)
                .flatMap { response -> route.response.get(response) }
                .recover { throw it.exception() }
        }
    }

    override fun <M : HttpMessage> any(): Kind2<ForSimpleLens, M, Unit> =
        AnyLens()

    override fun <M : HttpMessage> text(): Kind2<ForSimpleLens, M, String> =
        TextLens()

    override fun <M : HttpMessage, A, B> Kind2<ForSimpleLens, M, A>.and(
        other: Kind2<ForSimpleLens, M, B>) =
        PairLens(this.fix(), other.fix())

    override fun <M : HttpMessage, T> Kind2<ForSimpleLens, M, Unit>.but(
        other: Kind2<ForSimpleLens, M, T>) =
        ButLens(this.fix(), other.fix())

    override fun <M : HttpMessage, T, E> result(
        success: Kind2<ForSimpleLens, M, T>,
        failure: Kind2<ForSimpleLens, M, E>) =
        ResultMessageLens(success.fix(), failure.fix())

    override fun status(status: Status) =
        CheckStatusLens(status)

    override fun status() =
        StatusLens()

    override fun method(method: Method) =
        CheckMethodLens(method)

    override fun method() =
        MethodLens()

}
