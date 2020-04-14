package org.http4k.typesafe.routing

import com.natpryce.Result
import com.natpryce.flatMap
import com.natpryce.map
import com.natpryce.recover
import org.http4k.core.HttpHandler
import org.http4k.core.HttpMessage
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.routing.messages.ButLens
import org.http4k.typesafe.routing.messages.MappedLens
import org.http4k.typesafe.routing.messages.OrLens
import org.http4k.typesafe.routing.messages.ResultMessageLens
import org.http4k.typesafe.routing.messages.oneOf.OneOf2Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple2Lens

data class SimpleRoute<In, Out>(
    override val request: MessageLens<Request, In>,
    override val response: MessageLens<Response, Out>) :
    Route<In, Out, MessageLens<Request, In>, MessageLens<Response, Out>> {
}

/*
Server Route
------------------------------------------------------------------
 */

data class SimpleServerRoute<In, Out>(
    val route: SimpleRoute<In, Out>,
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

fun <In, Out> route(request: MessageLens<Request, In>,
                    response: MessageLens<Response, Out>) =
    SimpleRoute(request, response)

infix fun <In, Out> SimpleRoute<In, Out>.server(handler: (In) -> Out) =
    SimpleServerRoute(this, handler)

infix fun <In, Out> SimpleRoute<In, Out>.client(http: HttpHandler):
    (In) -> Out {
    val route = this@client
    return { param: In ->
        route.request.set(Request(GET, "/"), param)
            .map(http)
            .flatMap { response -> route.response.get(response) }
            .recover { throw it.exception() }
    }
}

infix fun <M : HttpMessage, A, B> MessageLens<M, A>.and(
    other: MessageLens<M, B>): Tuple2Lens<M, A, B> =
    Tuple2Lens(this, other)

fun <M : HttpMessage, A, B> MessageLens<M, A>.xor(other: MessageLens<M, B>) =
    OneOf2Lens(this, other)

fun <M : HttpMessage, T> MessageLens<M, T>.or(other: MessageLens<M, T>) =
    OrLens(listOf(this, other))

fun <M : HttpMessage, A, B> MessageLens<M, A>.map(
    getter: (A) -> Result<B, RoutingError>,
    setter: (B) -> A) =
    MappedLens(this, getter, setter)

infix fun <M : HttpMessage, T> MessageLens<M, Unit>.but(
    other: MessageLens<M, T>) =
    ButLens(this, other)

fun <M : HttpMessage, T, E> result(
    success: MessageLens<M, T>,
    failure: MessageLens<M, E>) =
    ResultMessageLens(success, failure)

@Suppress("ClassName")
object request : MessageRouting<Request>()

@Suppress("ClassName")
object response : MessageRouting<Response>()
