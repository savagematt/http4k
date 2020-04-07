package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiRouteInfo
import org.http4k.util.builders.BaseBuilder

class OpenApiRouteInfoDsl(original: OpenApiRouteInfo)
    : BaseBuilder<OpenApiRouteInfo, OpenApiRouteInfoDsl>(::OpenApiRouteInfoDsl) {
    var api = OpenApiObjectDsl(original.api)
    var route = OpenApiOperationInfoDsl(original.route)

    override operator fun invoke(f: OpenApiRouteInfoDsl.() -> Unit) = f(this)

    override fun build() = OpenApiRouteInfo(
        api.build(),
        route.build()
    )
}