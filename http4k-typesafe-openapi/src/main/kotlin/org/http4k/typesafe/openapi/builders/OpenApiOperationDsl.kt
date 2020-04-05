package org.http4k.typesafe.openapi.builders

import org.http4k.core.HttpMessage
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.openapi.ForOpenApiLens
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.OpenApiOperation
import org.http4k.typesafe.openapi.Real
import org.http4k.typesafe.openapi.fix
import org.http4k.typesafe.openapi.openapi
import org.http4k.typesafe.openapi.real
import org.http4k.util.Appendable
import org.http4k.util.builders.AppendableOrNull
import org.http4k.util.builders.MutableMapOrNull

class OpenApiOperationDsl(val original: OpenApiOperation) {
    // TODO responses
    var responses = original.responses
    var tags = AppendableOrNull(original.tags)
    var summary = original.summary
    var description = original.description
    var operationId = original.operationId
    var parameters = AppendableOrNull(original.parameters)
    // TODO requestBody
    var deprecated = original.deprecated
    var security = MutableMapOrNull(original.security)
    var extensions = Appendable.of(original.extensions)

    fun build() = OpenApiOperation(
        original.responses,
        tags.toList(),
        summary,
        description,
        operationId,
        parameters.toList(),
        original.requestBody,
        deprecated,
        security.toMap(),
        extensions.all.toList()
    )

    fun mapHeaders(fn: OpenApiParameterDsl.() -> Unit) {
        parameters = parameters.map {
            when (it) {
                is Real -> OpenApiParameterDsl(it.value).also(fn).build().real()
                // TODO: dereference ref and modify value
                else -> it
            }
        }
    }
}

infix fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, T>.meta(
    fn: OpenApiOperationDsl.() -> Unit): OpenApiLens<M, T> {
    val lens = this.fix()
    return lens openapi {
        lens.document(it).operation(fn)
    }
}
