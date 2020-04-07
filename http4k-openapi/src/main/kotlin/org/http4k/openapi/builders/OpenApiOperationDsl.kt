package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiOperation
import org.http4k.openapi.OpenApiRequestBody
import org.http4k.openapi.real
import org.http4k.util.Appendable
import org.http4k.util.builders.BaseBuilder
import org.http4k.util.builders.NullableListBuilder
import org.http4k.util.builders.NullableMapBuilder

class OpenApiOperationDsl(original: OpenApiOperation)
    : BaseBuilder<OpenApiOperation, OpenApiOperationDsl>(::OpenApiOperationDsl) {
    // TODO responses
    var responses = OpenApiResponsesDsl(original.responses)
    var tags = NullableListBuilder(original.tags, ::TagDsl)
    var summary = original.summary
    var description = original.description
    var operationId = original.operationId
    var parameters = NullableListBuilder(original.parameters) { ReferenceableDsl(it, ::OpenApiParameterDsl) }
    // TODO requestBody
    var requestBody = NullableDsl(
        original.requestBody,
        { OpenApiRequestBody.empty.real() },
        { ReferenceableDsl(it, ::OpenApiRequestBodyDsl) })
    var deprecated = original.deprecated
    var security = NullableMapBuilder(original.security, ::OpenApiOperationSecurityDsl)
    var extensions = Appendable.of(original.extensions)

    override operator fun invoke(f: OpenApiOperationDsl.() -> Unit) = f(this)

    override fun build() = OpenApiOperation(
        responses.build(),
        tags.build(),
        summary,
        description,
        operationId,
        parameters.build(),
        requestBody.build(),
        deprecated,
        security.build(),
        extensions.all.toList()
    )
}