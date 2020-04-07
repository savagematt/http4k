package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiResponse
import org.http4k.util.Appendable
import org.http4k.util.builders.BaseBuilder
import org.http4k.util.builders.NullableMapBuilder

class OpenApiResponseDsl(original: OpenApiResponse)
    : BaseBuilder<OpenApiResponse, OpenApiResponseDsl>(::OpenApiResponseDsl) {

    var description = original.description
    var content = NullableMapBuilder(original.content, ::OpenApiMediaTypeDsl)
    var headers = NullableMapBuilder(original.headers, ::OpenApiParameterDsl)
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiResponseDsl.() -> Unit) = f(this)

    override fun build() = OpenApiResponse(
        description,
        content.build(),
        headers.build(),
        extensions.all
    )

}