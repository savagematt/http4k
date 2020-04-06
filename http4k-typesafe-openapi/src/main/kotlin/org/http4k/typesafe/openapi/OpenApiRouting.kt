package org.http4k.typesafe.openapi

import com.natpryce.Result
import com.natpryce.flatMap
import com.natpryce.map
import com.natpryce.recover
import org.http4k.core.HttpHandler
import org.http4k.core.HttpMessage
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.functional.Kind
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.Routing
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.ServerRoute
import org.http4k.typesafe.routing.messages.ButLens
import org.http4k.typesafe.routing.messages.FirstLens
import org.http4k.typesafe.routing.messages.ResultMessageLens
import org.http4k.typesafe.routing.messages.oneOf.OneOf2Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple2Lens
import org.http4k.typesafe.routing.requests.paths.Path

/*
Path
------------------------------------------------------------------
 */

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
class ForOpenApiPath private constructor() {
    companion object
}

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <T> Kind<ForOpenApiPath, T>.fix() = this as OpenApiPath<T>

open class OpenApiPath<T>(
    private val delegate: Path<T>,
    private val documenter: (OpenApiRouteInfo) -> OpenApiRouteInfo = { it }) :
    Path<T> by delegate,
    Documentable<OpenApiRouteInfo>,
    Kind<ForOpenApiPath, T> {

    override fun document(doc: OpenApiRouteInfo): OpenApiRouteInfo =
        documenter(doc)

    override fun toString(): String = delegate.toString()
}

/*
Lenses
------------------------------------------------------------------
 */
/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
class ForOpenApiLens private constructor() {
    companion object
}

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, T>.fix() = this as OpenApiLens<M, T>

class OpenApiLens<M : HttpMessage, T>(
    private val original: MessageLens<M, T>,
    private val documenter: ((OpenApiRouteInfo) -> OpenApiRouteInfo)? = null) :
    MessageLens<M, T> by original,
    Documentable<OpenApiRouteInfo>,
    Kind2<ForOpenApiLens, M, T> {

    override fun document(doc: OpenApiRouteInfo): OpenApiRouteInfo =
        when (documenter) {
            null -> doc
            else -> documenter.invoke(doc)
        }

    override fun toString() = original.toString()

    infix fun documentation(more: ((OpenApiRouteInfo) -> OpenApiRouteInfo)) =
        OpenApiLens(this) { more(this.document(it)) }

    infix fun documentation(more: Documentable<OpenApiRouteInfo>) =
        OpenApiLens(this) { more.document(this.document(it)) }

    infix fun openapi(more: OpenApiRouteInfoDsl.() -> Unit) =
        OpenApiLens(this) { OpenApiRouteInfoDsl(this.document(it)).also(more).build() }
}

infix fun <M : HttpMessage, T> MessageLens<M, T>.openapi(
    docs: (OpenApiRouteInfoDsl.() -> Unit)) =
    this documentation { OpenApiRouteInfoDsl(it).also(docs).build() }

infix fun <M : HttpMessage, T> MessageLens<M, T>.documentation(
    docs: (OpenApiRouteInfo) -> OpenApiRouteInfo) =
    OpenApiLens(this, docs)

infix fun <M : HttpMessage, T> MessageLens<M, T>.documentation(
    docs: Documentable<OpenApiRouteInfo>) =
    OpenApiLens(this) { docs.document(it) }

/*
Routes
------------------------------------------------------------------
 */

/** @see [org.http4k.typesafe.functional.Kind2] */
class ForOpenApiRoute private constructor() {
    companion object
}

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <In, Out> Kind2<ForOpenApiRoute, In, Out>.fix() = this as OpenApiRoute<In, Out>

data class OpenApiRoute<In, Out>(
    override val request: OpenApiLens<Request, In>,
    override val response: OpenApiLens<Response, Out>,
    val extraDocs: (OpenApiRouteInfo) -> OpenApiRouteInfo = { it })
    : Kind2<ForOpenApiRoute, In, Out>,
    Route<In, Out, OpenApiLens<Request, In>, OpenApiLens<Response, Out>>,
    Documentable<OpenApiRouteInfo> {

    override fun document(doc: OpenApiRouteInfo): OpenApiRouteInfo =
        extraDocs(response.document(request.document(doc)))
}

/**
 *
 */
@Suppress("unused")
fun <In, Out> OpenApiRoute<In, Out>.doc(extraDocs: (OpenApiRouteInfo) -> OpenApiRouteInfo) =
    OpenApiRoute(this.request, this.response, extraDocs)

/*
Server Route
------------------------------------------------------------------
 */

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
class ForOpenApiServerRoute private constructor() {
    companion object
}

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <In, Out> Kind2<ForOpenApiServerRoute, In, Out>.fix() = this as OpenApiServerRoute<In, Out>

data class OpenApiServerRoute<In, Out>(
    val route: OpenApiRoute<In, Out>,
    val handler: (In) -> Out
) : ServerRoute, Kind2<ForOpenApiServerRoute, In, Out> {

    override fun handle(request: Request): Result<Response, RoutingError> =
        // try to extract handler parameter from request
        route.request.get(request)
            // pass the parameter to the handler function
            .map(handler)
            // inject the handler result into an empty Response
            .flatMap { route.response.set(Response(OK), it) }
}

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
            route.request.set(Request(GET, "/"), param)
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

