package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiMediaType
import org.http4k.typesafe.openapi.OpenApiSchema
import org.http4k.typesafe.openapi.real
import org.http4k.util.Appendable
import org.http4k.util.builders.MapBuilder

class OpenApiMediaTypeDsl(original: OpenApiMediaType)
    : BaseBuilder<OpenApiMediaType, OpenApiMediaTypeDsl>(::OpenApiMediaTypeDsl) {

    var examples = MapBuilder(original.examples, ::OpenApiBodyExampleDsl)
    var schema = NullableDsl(
        original.schema,
        { OpenApiSchema.empty.real() },
        { ReferenceableDsl(it, ::OpenApiSchemaDsl) })
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiMediaTypeDsl.() -> Unit) = f(this)

    override fun build() = OpenApiMediaType(
        examples.build(),
        schema.build(),
        extensions.all
    )

}