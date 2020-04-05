package org.http4k.typesafe.openapi

import org.http4k.core.ContentType
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.format.Json
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
import org.http4k.typesafe.routing.messages.body.JsonLens
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
            .asOpenApi(documentBody(clazz, TEXT_PLAIN))

    override fun <NODE : Any> json(json: Json<NODE>):
        Kind2<ForOpenApiLens, M, NODE> =
        openApiJson(clazz, json)

    override fun header(name: String) =
        HeaderReplaceLens<M>(name).asOpenApi(addParameter(name))

    override fun appendHeader(name: String) =
        HeaderAppendLens<M>(name).asOpenApi(addParameter(name))

    override fun appendHeaders(name: String) =
        HeadersAppendLens<M>(name).asOpenApi(addParameter(name))

    override fun headers(name: String) =
        HeadersReplaceLens<M>(name).asOpenApi(addParameter(name))

    override fun <T> Kind2<ForOpenApiLens, M, T?>.required(onFailure: () -> RoutingError)
        : Kind2<ForOpenApiLens, M, T> =
        this.fix().let { lens ->
            RequiredLens(lens, onFailure)
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
}

fun <M : HttpMessage, NODE : Any> openApiJson(clazz: KClass<M>, json: Json<NODE>) = JsonLens<M, NODE>(json)
    .asOpenApi(documentBody(clazz, APPLICATION_JSON))

fun addParameter(name: String): (OpenApiRouteInfo) -> OpenApiRouteInfo {
    return { info ->
        info.parameters {
            it + OpenApiParameter(
                HEADER,
                name).real()
        }
    }
}

fun <M : HttpMessage> documentBody(clazz: KClass<M>, contentType: ContentType): (OpenApiRouteInfo) -> OpenApiRouteInfo {
    val mediaType = OpenApiMediaType()

    val mediaTypes = mapOf(contentType to mediaType)

    return { info ->
        when (clazz) {
            Request::class ->
                info.requestBody {
                    OpenApiRequestBody(
                        "body",
                        mediaTypes,
                        required = true).real()
                }
            Response::class ->
                // TODO: what do we do if there are already responses?
                info.responses {
                    OpenApiResponses(default = OpenApiResponse("body", mediaTypes).real())
                }
            else -> throw IllegalArgumentException("$clazz was not a recognised HttpMessage type")
        }
    }
}

fun api(routes: List<OpenApiRoute<*, *>>): OpenApiObject =
    routes.fold(OpenApiObject.empty) { api, route ->
        route.document(OpenApiRouteInfo(api, OpenApiOperationInfo.empty)).let { (api, route) ->
            api.copy(paths = api.paths + route)
        }
    }

fun <M : HttpMessage> Kind2<ForOpenApiLens, M, *>.document() =
    this.fix().document(OpenApiRouteInfo.empty)

fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, T>.get(from: M) =
    this.fix().get(from)