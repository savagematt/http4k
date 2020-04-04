package org.http4k.typesafe.openapi

import com.natpryce.Result
import com.natpryce.flatMap
import com.natpryce.map
import com.natpryce.recover
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.HttpHandler
import org.http4k.core.HttpMessage
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.functional.Kind
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.Routing
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.ServerRoute
import org.http4k.typesafe.routing.messages.ButLens
import org.http4k.typesafe.routing.messages.ResultMessageLens
import org.http4k.typesafe.routing.messages.tuples.Tuple2Lens
import org.http4k.typesafe.routing.requests.paths.Path
import kotlin.reflect.KClass

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
    private val delegate: MessageLens<M, T>,
    private val documenter: (OpenApiRouteInfo) -> OpenApiRouteInfo = { it }) :
    MessageLens<M, T> by delegate,
    Documentable<OpenApiRouteInfo>,
    Kind2<ForOpenApiLens, M, T> {

    override fun document(doc: OpenApiRouteInfo): OpenApiRouteInfo =
        documenter(doc)

}

fun <M : HttpMessage, T> MessageLens<M, T>.asOpenApi(
    docs: (OpenApiRouteInfo) -> OpenApiRouteInfo = { it }) =
    OpenApiLens(this, docs)

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
        Tuple2Lens(this.fix(), other.fix())
            .asOpenApi(fold(this.fix(), other.fix()))

    override fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, Unit>.but(
        other: Kind2<ForOpenApiLens, M, T>) =
        ButLens(this.fix(), other.fix()).asOpenApi(
            fold(this.fix(), other.fix()))


    override fun <M : HttpMessage, T, E> result(
        success: Kind2<ForOpenApiLens, M, T>,
        failure: Kind2<ForOpenApiLens, M, E>) =
        ResultMessageLens(success.fix(), failure.fix())
            .asOpenApi(fold(success.fix(), failure.fix()))

    override val path = OpenApiPaths
    override val request = OpenApiRequestRouting
    override val response = OpenApiResponseRouting
}

fun <D> fold(vararg documentables: Documentable<D>): (D) -> D = { doc ->
    fold(doc, *documentables)
}

fun <D> fold(document: D, vararg documentables: Documentable<D>): D = documentables.fold(document)
{ doc, documentable ->
    documentable.document(doc)
}

fun <M : HttpMessage> documentTextLens(clazz: KClass<M>): (OpenApiRouteInfo) -> OpenApiRouteInfo {
    val header = OpenApiHeader("Content-Type", required = false)

    val mediaType = OpenApiMediaType(
        mapOf("text" to OpenApiBodyExample(OpenApiBodyExampleValue.Real("some text"))))

    val mediaTypes =
        mapOf(TEXT_PLAIN to mediaType)

    return {
        it.parameters { params ->
            params + header.real<OpenApiParameter>()
        }.let { info ->
            when (clazz) {
                Request::class ->
                    info.requestBody {
                        OpenApiRequestBody("text", mediaTypes).real()
                    }
                Response::class ->
                    // TODO: what do we do if there are already responses?
                    info.responses {
                        OpenApiResponses(default = OpenApiResponse("text", mediaTypes).real())
                    }
                else -> throw IllegalArgumentException("$clazz was not a recognised HttpMessage type")
            }
        }
    }
}
