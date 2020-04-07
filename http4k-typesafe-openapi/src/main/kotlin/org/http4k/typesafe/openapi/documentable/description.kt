package org.http4k.typesafe.openapi.documentable

import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.openapi.ForOpenApiLens
import org.http4k.typesafe.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.typesafe.openapi.fix

inline fun <reified M : HttpMessage, T> Kind2<ForOpenApiLens, M, T>.description(value: String) =
    this.fix() openapi descriptionOf<M>(value)

/**
 * Sets description on requests or responses
 */
inline fun <reified M : HttpMessage> descriptionOf(value: String)
    : OpenApiRouteInfoDsl.() -> Unit = {
    route {
        operation {
            when (M::class) {
                Request::class -> {
                    requestBody {
                        description = value
                    }
                }
                Response::class -> {
                    responses {
                        default {
                            mapNullable {
                                mapReferenceable {
                                    description = value
                                }
                            }
                        }
                        byStatus.map {
                            mapReferenceable {
                                description = value
                            }
                        }
                    }
                }
            }
        }
    }
}