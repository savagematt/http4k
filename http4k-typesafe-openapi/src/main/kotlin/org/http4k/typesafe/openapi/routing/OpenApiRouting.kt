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
import org.http4k.util.functional.Kind2
import org.http4k.typesafe.openapi.ForOpenApiLens
import org.http4k.typesafe.openapi.ForOpenApiPath
import org.http4k.typesafe.openapi.ForOpenApiRoute
import org.http4k.typesafe.openapi.ForOpenApiServerRoute
import org.http4k.typesafe.openapi.OpenApiRoute
import org.http4k.typesafe.openapi.OpenApiServerRoute
import org.http4k.typesafe.openapi.documentation
import org.http4k.typesafe.openapi.fix
import org.http4k.typesafe.openapi.fold
import org.http4k.typesafe.openapi.or
import org.http4k.typesafe.routing.Routing
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.ButLens
import org.http4k.typesafe.routing.messages.FirstLens
import org.http4k.typesafe.routing.messages.MappedLens
import org.http4k.typesafe.routing.messages.ResultMessageLens
import org.http4k.typesafe.routing.messages.oneOf.OneOf2Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple2Lens

object OpenApiRouting : Routing<ForOpenApiServerRoute, ForOpenApiRoute, ForOpenApiLens, ForOpenApiPath> {
    override fun <In, Out> route(
        request: Kind2<ForOpenApiLens, Request, In>,
        response: Kind2<ForOpenApiLens, Response, Out>) =
        OpenApiRoute(request.fix(), response.fix())

    override fun <In, Out> Kind2<ForOpenApiRoute, In, Out>.serve(
        handler: (In) -> Out) =
        OpenApiServerRoute(this.fix(), handler).fix()

    override fun <In, Out> Kind2<ForOpenApiRoute, In, Out>.consume(
        http: HttpHandler): (In) -> Out {
        val route = this@consume.fix()
        return { param: In ->
            route.request.set(Request(Method.GET, "/"), param)
                .map(http)
                .flatMap { response -> route.response.get(response) }
                .recover { throw it.exception() }
        }
    }

    override fun <M : HttpMessage, A, B> Kind2<ForOpenApiLens, M, A>.and(
        other: Kind2<ForOpenApiLens, M, B>) =
        Tuple2Lens(this.fix(), other.fix()) documentation fold(this.fix(), other.fix())

    override fun <M : HttpMessage, A, B> Kind2<ForOpenApiLens, M, A>.or(other: Kind2<ForOpenApiLens, M, B>) =
        OneOf2Lens(this.fix(), other.fix())
            .documentation {
                fix().document(it) or other.fix().document(it)
            }

    override fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, T>.alternatively(other: Kind2<ForOpenApiLens, M, T>) =
        FirstLens(listOf(this.fix(), other.fix()))
            .documentation {
                fix().document(it) or other.fix().document(it)
            }

    override fun <M : HttpMessage, A, B> Kind2<ForOpenApiLens, M, A>.map(getter: (A) -> Result<B, RoutingError>, setter: (B) -> A) =
        MappedLens(this.fix(), getter, setter) documentation this.fix()

    override fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, Unit>.but(
        other: Kind2<ForOpenApiLens, M, T>) =
        ButLens(this.fix(), other.fix()) documentation fold(this.fix(), other.fix())


    override fun <M : HttpMessage, T, E> result(
        success: Kind2<ForOpenApiLens, M, T>,
        failure: Kind2<ForOpenApiLens, M, E>) =
        ResultMessageLens(success.fix(), failure.fix()) documentation fold(success.fix(), failure.fix())

    override val path = OpenApiPaths
    override val request = OpenApiRequestRouting
    override val response = OpenApiResponseRouting
}