package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiComponents
import org.http4k.util.Appendable
import org.http4k.util.builders.NullableMapBuilder

class OpenApiComponentsDsl(original: OpenApiComponents)
    : BaseBuilder<OpenApiComponents, OpenApiComponentsDsl>(::OpenApiComponentsDsl) {
    var security = NullableMapBuilder(
        original.securitySchemes) {
        ReferenceableDsl(it, ::OpenApiSecuritySchemeDsl)
    }
    var schemas = NullableMapBuilder(original.schemas, ::OpenApiSchemaDsl)
    var extensions = Appendable.of(original.extensions)

    override operator fun invoke(f: OpenApiComponentsDsl.() -> Unit) = f(this)

    override fun OpenApiComponents.invoke(f: OpenApiComponentsDsl.() -> Unit) =
        OpenApiComponentsDsl(this).also(f).build()

    override fun build() = OpenApiComponents(
        security.build(),
        schemas.build(),
        extensions.all
    )

}