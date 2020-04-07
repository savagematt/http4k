package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiObject
import org.http4k.util.Appendable
import org.http4k.util.builders.BaseBuilder

class OpenApiObjectDsl(original: OpenApiObject)
    : BaseBuilder<OpenApiObject, OpenApiObjectDsl>(::OpenApiObjectDsl) {
    var info: OpenApiInfoDsl = OpenApiInfoDsl(original.info)
    var paths = Appendable.of(original.paths)
    var components = OpenApiComponentsDsl(original.components)
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiObjectDsl.() -> Unit) = f(this)

    override fun build() = OpenApiObject(
        info.build(),
        paths.all,
        components.build(),
        extensions.all
    )
}