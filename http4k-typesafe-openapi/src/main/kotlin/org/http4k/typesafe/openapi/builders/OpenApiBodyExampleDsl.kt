package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiBodyExample
import org.http4k.util.Appendable

class OpenApiBodyExampleDsl(original: OpenApiBodyExample)
    : BaseBuilder<OpenApiBodyExample, OpenApiBodyExampleDsl>(::OpenApiBodyExampleDsl) {

    var value = OpenApiBodyExampleValueDsl(original.value)
    var summary = original.summary
    var description = original.description
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiBodyExampleDsl.() -> Unit) = f(this)

    override fun build() = OpenApiBodyExample(
        value.build(),
        summary,
        description,
        extensions.all
    )
}