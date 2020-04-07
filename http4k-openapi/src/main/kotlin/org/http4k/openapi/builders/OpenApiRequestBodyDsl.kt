package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiRequestBody
import org.http4k.util.Appendable
import org.http4k.util.builders.BaseBuilder
import org.http4k.util.builders.NullableMapBuilder

class OpenApiRequestBodyDsl(original: OpenApiRequestBody)
    : BaseBuilder<OpenApiRequestBody, OpenApiRequestBodyDsl>(::OpenApiRequestBodyDsl) {
    var description = original.description
    var content = NullableMapBuilder(original.content, ::OpenApiMediaTypeDsl)
    var required = original.required
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiRequestBodyDsl.() -> Unit) = f(this)

    override fun build() = OpenApiRequestBody(
        description,
        content.build(),
        required,
        extensions.all
    )

}