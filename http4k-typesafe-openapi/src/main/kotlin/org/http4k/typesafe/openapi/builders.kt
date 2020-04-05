package org.http4k.typesafe.openapi

import org.http4k.core.HttpMessage
import org.http4k.typesafe.functional.Kind2
import org.http4k.util.Appendable

/**
 * toList() will return null if the initial value was null and
 * no items have been assigned
 */
class AppendableOrNull<T>(private val initial: List<T>?) {
    var all: MutableList<T>? = null

    fun all() = all ?: (initial?.toMutableList() ?: mutableListOf())

    operator fun plusAssign(t: T) {
        all() += t
    }

    operator fun plusAssign(t: Collection<T>) {
        all() += t
    }

    fun map(fn: (T) -> T): AppendableOrNull<T> {
        all = all().map(fn).toMutableList()
        return this
    }

    fun toList() = all?.toList() ?: initial
}

//class OpenApiResponsesDsl(original: OpenApiResponses) {
//    val default: Referenceable<OpenApiResponse>? = original.default
//    val byStatus: MutableMap<Status, Referenceable<OpenApiResponse>>? = original.
//}

class OpenApiParameterDsl(val original: OpenApiParameter) {
    var in_ = original.in_
    var name = original.name
    var required = original.required
    var deprecated = original.deprecated
    var description = original.description
    // TODO: schema
    var extensions = Appendable(original.extensions.toMutableList())

    fun build() = OpenApiParameter(
        in_,
        name,
        required,
        deprecated,
        description,
        original.schema,
        extensions.all
    )
}

class OpenApiOperationDsl(original: OpenApiOperationInfo) {
    private val operation = original.operation

    var method = original.method
    var path = original.path

    // TODO responses
    var responses = operation.responses
    var tags = AppendableOrNull(operation.tags)
    var summary = operation.summary
    var description = operation.description
    var operationId = operation.operationId
    var parameters = AppendableOrNull(operation.parameters)
    // TODO requestBody
    var deprecated = operation.deprecated
    // TODO security
    var extensions = Appendable(operation.extensions.toMutableList())

    fun build() = OpenApiOperationInfo(
        method,
        path,
        OpenApiOperation(
            operation.responses,
            tags.toList(),
            summary,
            description,
            operationId,
            parameters.toList(),
            operation.requestBody,
            deprecated,
            operation.security,
            extensions.all.toList()
        )

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
            lens.document(it).let { docs ->
                docs.route { route ->
                    OpenApiOperationDsl(route).apply(fn).build()
                }
            }
        }
    }
