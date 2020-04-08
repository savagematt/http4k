package org.http4k.typesafe.openapi.documentable

import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.typesafe.openapi.OpenApiLens

/**
 * Returns an [org.http4k.typesafe.openapi.OpenApiLens] with request/response
 * descriptions set as specified in the docs.
 *
 * https://medium.com/tompee/idiomatic-kotlin-lambdas-with-receiver-and-dsl-3cd3348e1235
 */
inline fun <reified M : HttpMessage, T> OpenApiLens<M, T>.description(value: String) =
    this openapi descriptionOf<M>(value)

/**
 * Sets description on requests or responses
 *
 * https://medium.com/tompee/idiomatic-kotlin-lambdas-with-receiver-and-dsl-3cd3348e1235
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