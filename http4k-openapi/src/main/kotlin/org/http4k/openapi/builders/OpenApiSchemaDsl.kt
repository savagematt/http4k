package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiSchema
import org.http4k.util.Appendable
import org.http4k.util.builders.BaseBuilder

class OpenApiSchemaDsl(original: OpenApiSchema)
    : BaseBuilder<OpenApiSchema, OpenApiSchemaDsl>(::OpenApiSchemaDsl) {
    override fun invoke(f: OpenApiSchemaDsl.() -> Unit) = f(this)

    private var raw: Raw? = null

    init {
        when (original) {
            is OpenApiSchema.Raw<*> -> {
                this.raw = Raw(original)
            }
        }
    }

    override fun build() =
        raw?.build()
            ?: throw IllegalStateException("Programmer error in ${this::class.simpleName}" +
                "exactly one builder should have a value")

    class Raw(original: OpenApiSchema.Raw<*>)
        : BaseBuilder<OpenApiSchema.Raw<*>, Raw>(OpenApiSchemaDsl::Raw) {
        var schema = original.schema
        var extensions = Appendable.of(original.extensions)

        override fun invoke(f: Raw.() -> Unit) = f(this)

        override fun build() = OpenApiSchema.Raw(
            schema,
            extensions.all
        )

    }

}