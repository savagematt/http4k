package org.http4k.typesafe.openapi.routing

import org.http4k.core.ContentType
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.format.Json
import org.http4k.openapi.OpenApiMediaType
import org.http4k.openapi.OpenApiParameter
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.openapi.ParameterLocation.HEADER
import org.http4k.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.openapi.real
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.documentation
import org.http4k.typesafe.openapi.openapi
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.AnyLens
import org.http4k.typesafe.routing.messages.HeaderAppendLens
import org.http4k.typesafe.routing.messages.HeaderReplaceLens
import org.http4k.typesafe.routing.messages.HeadersAppendLens
import org.http4k.typesafe.routing.messages.HeadersReplaceLens
import org.http4k.typesafe.routing.messages.NothingLens
import org.http4k.typesafe.routing.messages.RequiredLens
import org.http4k.typesafe.routing.messages.UnitLens
import org.http4k.typesafe.routing.messages.body.JsonLens
import org.http4k.typesafe.routing.messages.body.TextLens
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

open class OpenApiMessageRouting<M : HttpMessage>(private val clazz: KClass<M>) {
    fun any() =
        AnyLens<M>().openapi({})

    fun nothing() =
        NothingLens<M>().openapi({})

    fun unit() =
        UnitLens<M>().openapi({})

    fun text():
        OpenApiLens<M, String> =
        TextLens<M>() openapi documentBody(clazz, TEXT_PLAIN)

    fun <NODE : Any> json(json: Json<NODE>):
        OpenApiLens<M, NODE> =
        openApiJson(clazz, json)

    fun header(name: String) =
        HeaderReplaceLens<M>(name) openapi headerParameter(name)

    fun appendHeader(name: String) =
        HeaderAppendLens<M>(name) openapi headerParameter(name)

    fun appendHeaders(name: String) =
        HeadersAppendLens<M>(name) openapi headerParameter(name)

    fun headers(name: String) =
        HeadersReplaceLens<M>(name) openapi headerParameter(name)

    fun <T> required(lens: OpenApiLens<M, T?>, onFailure: (() -> RoutingError)? = null)
        : OpenApiLens<M, T> =
        lens.let {
            RequiredLens(it, onFailure)
                .documentation(it)
                .openapi {
                    /**
                     * We don't know what the previous lens is, so here we just make
                     * everything it could possibly have added to our docs "required"
                     */
                    /**
                     * We don't know what the previous lens is, so here we just make
                     * everything it could possibly have added to our docs "required"
                     */
                    route {
                        operation {
                            requestBody.map {
                                mapReferenceable {
                                    required = true
                                }
                            }
                            parameters.map {
                                mapReferenceable {
                                    required = true
                                }
                            }
                        }
                    }

                }

        }
}

@Suppress("UNCHECKED_CAST")
inline fun <T, reified M : HttpMessage> OpenApiLens<M, T?>.required(noinline onFailure: (() -> RoutingError)? = null)
    : OpenApiLens<M, T> {
    // TODO: this is obviously a horror show
    if (M::class.isSubclassOf(Request::class)) {
        val required = request.required(this as OpenApiLens<Request, T?>, onFailure)
        return required as OpenApiLens<M, T>
    } else {
        val required = response.required(this as OpenApiLens<Response, T?>, onFailure)
        return required as OpenApiLens<M, T>
    }

}

/**
 * Returns a simple json lens, with an empty request or response body marked as application/json
 */
fun <M : HttpMessage, NODE : Any> openApiJson(clazz: KClass<M>, json: Json<NODE>): OpenApiLens<M, NODE> =
    JsonLens<M, NODE>(json) openapi documentBody(clazz, APPLICATION_JSON)

private fun headerParameter(name: String): OpenApiRouteInfoDsl.() -> Unit = {
    route {
        operation {
            parameters += OpenApiParameter(
                HEADER,
                name).real()
        }
    }
}

/**
 * Returns an extension method that adds an empty request or response body
 * of the specified content type
 */
fun <M : HttpMessage> documentBody(clazz: KClass<M>, contentType: ContentType)
    : OpenApiRouteInfoDsl.() -> Unit = {

    val mediaType = OpenApiMediaType()

    val newContent = contentType to mediaType

    route {
        operation {
            when (clazz) {
                Request::class -> {
                    requestBody {
                        mapNullable {
                            mapReferenceable {
                                content += newContent
                                required = true
                            }
                        }
                    }
                }
                Response::class -> {
                    responses {
                        default {
                            mapNullable {
                                mapReferenceable {
                                    content += newContent
                                }
                            }
                        }
                    }
                }
                else -> throw IllegalArgumentException("$clazz was not a recognised HttpMessage type")
            }

        }
    }

}


fun <M : HttpMessage> OpenApiLens<M, *>.document() =
    this.document(OpenApiRouteDocs.empty)
