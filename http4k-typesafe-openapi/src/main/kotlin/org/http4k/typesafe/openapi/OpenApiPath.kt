package org.http4k.typesafe.openapi

import org.http4k.openapi.OpenApiRouteInfo
import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.util.Documentable

/**
 * Wraps a normal path and adds some openapi documentation
 */
open class OpenApiPath<T>(
    private val delegate: Path<T>,
    private val documenter: (OpenApiRouteInfo) -> OpenApiRouteInfo = { it }) :
    Path<T> by delegate,
    Documentable<OpenApiRouteInfo> {

    override fun document(doc: OpenApiRouteInfo): OpenApiRouteInfo =
        documenter(doc)

    override fun toString(): String = delegate.toString()
}
