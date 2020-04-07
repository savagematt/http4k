package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiParameter
import org.http4k.openapi.ParameterLocation
import org.http4k.util.Appendable
import org.http4k.util.builders.BaseBuilder

class OpenApiParameterDsl(original: OpenApiParameter)
    : BaseBuilder<OpenApiParameter, OpenApiParameterDsl>(::OpenApiParameterDsl) {

    @Suppress("PropertyName")
    var in_ = original.in_
    var name = original.name
    var required = original.required
    var deprecated = original.deprecated
    var description = original.description
    // TODO: schema
    val schema = original.schema
    var extensions = Appendable.of(original.extensions)

    override operator fun invoke(f: OpenApiParameterDsl.() -> Unit) = f(this)

    override fun build() = OpenApiParameter(
        in_,
        name,
        /**
         * Path params are always required
         */
        /**
         * Path params are always required
         */
        /**
         * Path params are always required
         */
        /**
         * Path params are always required
         */
        if (in_ == ParameterLocation.PATH) true else required,
        deprecated,
        description,
        schema,
        extensions.all
    )
}