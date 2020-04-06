package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiSchema
import org.http4k.util.Appendable

class OpenApiSchemaDsl(original: OpenApiSchema)
    : BaseBuilder<OpenApiSchema, OpenApiSchemaDsl>(::OpenApiSchemaDsl) {
    override fun invoke(f: OpenApiSchemaDsl.() -> Unit) = f(this)

    private var raw: Raw? = null

    init {
        when (original) {
            is OpenApiSchema.Raw -> {
                this.raw = Raw(original)
            }
        }
    }

    override fun build() =
        raw?.build()
            ?: throw IllegalStateException("Programmer error in ${this::class.simpleName}" +
                "exactly one builder should have a value")

    class Raw(original: OpenApiSchema.Raw)
        : BaseBuilder<OpenApiSchema.Raw, Raw>(::Raw) {
        var schema = original.schema
        var extensions = Appendable.of(original.extensions)

        override fun invoke(f: Raw.() -> Unit) = f(this)

        override fun build() = OpenApiSchema.Raw(
            schema,
            extensions.all
        )

    }

}