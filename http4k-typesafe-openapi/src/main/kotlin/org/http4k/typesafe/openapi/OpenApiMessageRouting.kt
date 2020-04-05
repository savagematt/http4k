package org.http4k.typesafe.openapi

import org.http4k.core.HttpMessage
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.openapi.ParameterLocation.HEADER
import org.http4k.typesafe.routing.MessageRouting
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.AnyLens
import org.http4k.typesafe.routing.messages.HeaderAppendLens
import org.http4k.typesafe.routing.messages.HeaderReplaceLens
import org.http4k.typesafe.routing.messages.HeadersAppendLens
import org.http4k.typesafe.routing.messages.HeadersReplaceLens
import org.http4k.typesafe.routing.messages.NothingLens
import org.http4k.typesafe.routing.messages.RequiredLens
import org.http4k.typesafe.routing.messages.body.TextLens
import kotlin.reflect.KClass

open class OpenApiMessageRouting<M : HttpMessage>(private val clazz: KClass<M>) : MessageRouting<M, ForOpenApiLens> {
    override fun any() =
        AnyLens<M>().asOpenApi()

    override fun nothing() =
        NothingLens<M>().asOpenApi()

    override fun text():
        Kind2<ForOpenApiLens, M, String> =
        TextLens<M>()
            .asOpenApi(documentTextLens(clazz))

    override fun header(name: String) =
        HeaderReplaceLens<M>(name).asOpenApi(addParameter(name))

    override fun appendHeader(name: String) =
        HeaderAppendLens<M>(name).asOpenApi(addParameter(name))

    override fun appendHeaders(name: String) =
        HeadersAppendLens<M>(name).asOpenApi(addParameter(name))

    override fun headers(name: String) =
        HeadersReplaceLens<M>(name).asOpenApi(addParameter(name))

    override fun <T> Kind2<ForOpenApiLens, M, T?>.required(onFailure: () -> RoutingError) =
        RequiredLens(this.fix(), onFailure)
            .asOpenApi()
}

fun addParameter(name: String): (OpenApiRouteInfo) -> OpenApiRouteInfo {
    return { info ->
        info.parameters {
            it + OpenApiParameter(
                HEADER,
                name).real()
        }
    }
}

fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, T?>.required(): OpenApiLens<M, T> {
    val lens: OpenApiLens<M, T?> = this.fix()
    return RequiredLens(lens) { RoutingError.RouteFailed("$this is required", Response(BAD_REQUEST)) }
        .asOpenApi {
            /**
             * We don't know what the previous lens is, so here we just make
             * everything it could possibly have added to our docs "required"
             */
            lens.document(it).let { info ->
                info.mapParameters { parameter ->
                    when (parameter) {
                        is Real -> Real(parameter.value.copy(required = true))
                        else -> parameter
                    }
                }.requestBody { requestBody ->
                    when (requestBody) {
                        is Real -> Real(requestBody.value.copy(required = true))
                        else -> requestBody
                    }
                }
            }
        }
}

fun api(routes: List<OpenApiRoute<*, *>>): OpenApiObject =
    routes.fold(OpenApiObject.empty) { api, route ->
        route.document(OpenApiRouteInfo(api, OpenApiOperationInfo.empty)).let { (api, route) ->
            api.copy(paths = api.paths + route)
        }
    }

fun OpenApiLens<*, *>.document() =
    this.document(OpenApiRouteInfo(OpenApiObject.empty, OpenApiOperationInfo.empty))