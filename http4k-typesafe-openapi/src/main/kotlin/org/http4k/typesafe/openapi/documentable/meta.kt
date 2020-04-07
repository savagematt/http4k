package org.http4k.typesafe.openapi.documentable

import org.http4k.core.HttpMessage
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.openapi.ForOpenApiLens
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.builders.OpenApiOperationDsl
import org.http4k.typesafe.openapi.fix

infix fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, T>.meta(
    fn: OpenApiOperationDsl.() -> Unit): OpenApiLens<M, T> {
    val lens = this.fix()
    return lens openapi {
        route {
            operation(fn)
        }
    }
}
