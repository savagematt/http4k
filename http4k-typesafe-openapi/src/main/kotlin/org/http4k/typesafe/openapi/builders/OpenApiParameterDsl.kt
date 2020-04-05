package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiParameter
import org.http4k.typesafe.openapi.ParameterLocation
import org.http4k.util.Appendable

class OpenApiParameterDsl(val original: OpenApiParameter) {
    var name = original.name
    var required = original.required
    var deprecated = original.deprecated
    var description = original.description
    // TODO: schema
    var extensions = Appendable.of(original.extensions)

    fun build() = OpenApiParameter(
        original.in_,
        name,
        /**
         * Path params are always required
         */
        /**
         * Path params are always required
         */
        if (original.in_ == ParameterLocation.PATH) true else required,
        deprecated,
        description,
        original.schema,
        extensions.all
    )
}