package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.util.builders.BaseBuilder

class OpenApiRouteInfoDsl(original: OpenApiRouteDocs)
    : BaseBuilder<OpenApiRouteDocs, OpenApiRouteInfoDsl>(::OpenApiRouteInfoDsl) {
    var api = OpenApiObjectDsl(original.api)
    var route = OpenApiOperationInfoDsl(original.operation)

    override operator fun invoke(f: OpenApiRouteInfoDsl.() -> Unit) = f(this)

    override fun build() = OpenApiRouteDocs(
        api.build(),
        route.build()
    )
}