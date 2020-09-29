package org.http4k.typesafe.openapi.routing

import com.natpryce.Result
import com.natpryce.flatMap
import com.natpryce.map
import com.natpryce.recover
import org.http4k.core.HttpHandler
import org.http4k.core.HttpMessage
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.openapi.OpenApiRouteInfo
import org.http4k.typesafe.openapi.OpenApiTuple2
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.OpenApiServerRoute
import org.http4k.typesafe.openapi.documentation
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.IgnoreUnitLens
import org.http4k.typesafe.routing.messages.MappedLens
import org.http4k.typesafe.routing.messages.ResultMessageLens
import org.http4k.typesafe.routing.fold


infix fun <In, Out> Route<In, Out, OpenApiRouteInfo>.server(
    handler: (In) -> Out) =
    OpenApiServerRoute(this, handler)

infix fun <In, Out> Route<In, Out, OpenApiRouteInfo>.client(
    http: HttpHandler): (In) -> Out {
    val route = this@client
    return { param: In ->
        route.request.set(Request(Method.GET, "/"), param)
            .map(http)
            .flatMap { response -> route.response.get(response) }
            .recover { throw it.exception() }
    }
}

infix fun <M : HttpMessage, A, B> OpenApiLens<M, A>.and(
    other: OpenApiLens<M, B>): OpenApiTuple2<M, A, B> =
    OpenApiTuple2(this, other)

fun <M : HttpMessage, A, B> OpenApiLens<M, A>.map(getter: (A) -> Result<B, RoutingError>, setter: (B) -> A) =
    MappedLens(this, getter, setter) documentation this

infix fun <M : HttpMessage, T> OpenApiLens<M, Unit>.and(
    other: OpenApiLens<M, T>) =
    IgnoreUnitLens(this, other) documentation fold(this, other)


fun <M : HttpMessage, T, E> result(
    success: OpenApiLens<M, T>,
    failure: OpenApiLens<M, E>) =
    ResultMessageLens(success, failure) documentation fold(success, failure)

@Suppress("ClassName")
object request : OpenApiMessageRouting<Request>(Request::class)

@Suppress("ClassName")
object response : OpenApiMessageRouting<Response>(Response::class)
