package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiRouteInfo

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