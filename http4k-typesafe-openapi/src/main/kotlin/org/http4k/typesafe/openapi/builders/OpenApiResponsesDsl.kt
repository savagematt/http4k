package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiResponse
import org.http4k.typesafe.openapi.OpenApiResponses
import org.http4k.typesafe.openapi.real
import org.http4k.util.Appendable
import org.http4k.util.builders.NullableMapBuilder

class OpenApiResponsesDsl(original: OpenApiResponses)
    : BaseBuilder<OpenApiResponses, OpenApiResponsesDsl>(::OpenApiResponsesDsl) {

    var default = NullableDsl(
        original.default,
        { OpenApiResponse.empty.real() },
        { ReferenceableDsl(it, ::OpenApiResponseDsl) })

    var byStatus = NullableMapBuilder(
        original.byStatus) {
        ReferenceableDsl(it, ::OpenApiResponseDsl)
    }

    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiResponsesDsl.() -> Unit) = f(this)

    override fun build() = OpenApiResponses(
        default.build(),
        byStatus.build(),
        extensions.all
    )

}