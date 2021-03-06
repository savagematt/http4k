package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiInfo
import org.http4k.util.Appendable
import org.http4k.util.builders.BaseBuilder

class OpenApiInfoDsl(original: OpenApiInfo)
    : BaseBuilder<OpenApiInfo, OpenApiInfoDsl>(::OpenApiInfoDsl){
    var title = original.title
    var version = original.version
    var description = original.description
    var termsOfService = original.termsOfService
    var contact = original.contact
    var license = original.license
    var extensions = Appendable.of(original.extensions)

    override operator fun invoke(f: OpenApiInfoDsl.() -> Unit) = f(this)

    override fun build() = OpenApiInfo(
        title,
        version,
        description,
        termsOfService,
        contact,
        license,
        extensions.all
    )

}