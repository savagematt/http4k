package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiComponents
import org.http4k.util.Appendable
import org.http4k.util.builders.MutableMapOrNull

class OpenApiComponentsDsl(original: OpenApiComponents) {
    var security = MutableMapOrNull(original.securitySchemes)
    var schemas = MutableMapOrNull(original.schemas)
    var extensions = Appendable.of(original.extensions)

    operator fun invoke(f: OpenApiComponentsDsl.() -> Unit) = f(this)
    fun build() = OpenApiComponents(
        security.toMap(),
        schemas.toMap()
    )
}