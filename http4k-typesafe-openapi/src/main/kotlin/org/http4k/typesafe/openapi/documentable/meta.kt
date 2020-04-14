package org.http4k.typesafe.openapi.documentable

import org.http4k.core.HttpMessage
import org.http4k.typesafe.openapi.OpenApiAdapter
import org.http4k.openapi.builders.OpenApiOperationDsl
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.openapi

/**
 * Modifies [org.http4k.openapi.OpenApiOperation]
 *
 * https://medium.com/tompee/idiomatic-kotlin-lambdas-with-receiver-and-dsl-3cd3348e1235
 */
infix fun <M : HttpMessage, T> OpenApiLens<M, T>.meta(
    fn: OpenApiOperationDsl.() -> Unit): OpenApiLens<M, T> {
    val lens = this
    return lens openapi {
        route {
            operation(fn)
        }
    }
}
