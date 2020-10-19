package org.http4k.typesafe.openapi.routing

import com.natpryce.Result
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.OpenApiTuple2
import org.http4k.typesafe.openapi.documentation
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.fold
import org.http4k.typesafe.routing.messages.IgnoreUnitLens
import org.http4k.typesafe.routing.messages.LensValueMapper
import org.http4k.typesafe.routing.messages.MappedLens
import org.http4k.typesafe.routing.messages.ResultMessageLens
import org.http4k.typesafe.routing.messages.mapper

infix fun <M : HttpMessage, A, B> OpenApiLens<M, A>.with(
    other: OpenApiLens<M, B>): OpenApiTuple2<M, A, B> =
    OpenApiTuple2(this, other)

infix fun <M : HttpMessage, T> OpenApiLens<M, Unit>.with(
    other: OpenApiLens<M, T>) =
    IgnoreUnitLens(this, other) documentation fold(this, other)

interface LensWrapper<M : HttpMessage, A, B, D> {
    operator fun invoke(wrapped: MessageLens<M, A, D>): MessageLens<M, B, D>
}

infix fun <M : HttpMessage, A, B> OpenApiLens<M, A>.with(
    wrapper: LensWrapper<M, A, B, OpenApiRouteDocs>) =
    wrapper(this)

fun <M : HttpMessage, A> OpenApiLens<M, A>.map(getter: (A) -> Result<A, RoutingError>) =
    MappedLens(this, mapper(getter, { it })) documentation this

fun <M : HttpMessage, A, B> OpenApiLens<M, A>.map(mapper: LensValueMapper<A, B>) =
    MappedLens(this, mapper) documentation this

fun <M : HttpMessage, A, B> OpenApiLens<M, A>.map(getter: (A) -> Result<B, RoutingError>, setter: (B) -> A) =
    MappedLens(this, mapper(getter, setter)) documentation this

fun <M : HttpMessage, T, E> result(
    success: OpenApiLens<M, T>,
    failure: OpenApiLens<M, E>) =
    ResultMessageLens(success, failure) documentation fold(success, failure)

@Suppress("ClassName")
object request : OpenApiMessageRouting<Request>(Request::class)

@Suppress("ClassName")
object response : OpenApiMessageRouting<Response>(Response::class)
