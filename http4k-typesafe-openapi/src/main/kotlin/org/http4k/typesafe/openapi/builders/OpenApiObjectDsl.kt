package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiObject
import org.http4k.util.Appendable

fun OpenApiObject.modify(f: OpenApiObjectDsl.() -> Unit) = OpenApiObjectDsl(this).also { f(it) }.build()

fun OpenApiObject.modifyComponents(f: OpenApiComponentsDsl.() -> Unit) =
    this.copy(components = OpenApiComponentsDsl(this.components).also { f(it) }.build())

class OpenApiObjectDsl(original: OpenApiObject) {
    var info: OpenApiInfoDsl = OpenApiInfoDsl(original.info)
    var paths = Appendable.of(original.paths)
    var components = OpenApiComponentsDsl(original.components)
    var extensions = Appendable.of(original.extensions)

    fun build() = OpenApiObject(
        info.build(),
        paths.all,
        components.build(),
        extensions.all
    )
}