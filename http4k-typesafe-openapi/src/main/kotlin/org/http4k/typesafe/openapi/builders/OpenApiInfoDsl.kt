package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiInfo
import org.http4k.util.Appendable

class OpenApiInfoDsl(original: OpenApiInfo) {
    var title = original.title
    var version = original.version
    var description = original.description
    var termsOfService = original.termsOfService
    var contact = original.contact
    var license = original.license
    var extensions = Appendable.of(original.extensions)

    fun build() = OpenApiInfo(
        title,
        version,
        description,
        termsOfService,
        contact,
        license,
        extensions.all
    )

    operator fun invoke(fn: OpenApiInfoDsl.() -> Unit) = fn(this)
}