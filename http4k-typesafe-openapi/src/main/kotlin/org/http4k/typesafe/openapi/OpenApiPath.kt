package org.http4k.typesafe.openapi

import org.http4k.openapi.OpenApiRouteInfo
import org.http4k.util.functional.Kind
import org.http4k.typesafe.routing.requests.paths.Path

/** @see [org.http4k.util.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
class ForOpenApiPath private constructor() {
    companion object
}

/** @see [org.http4k.util.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <T> Kind<ForOpenApiPath, T>.fix() = this as OpenApiPath<T>

open class OpenApiPath<T>(
    private val delegate: Path<T>,
    private val documenter: (OpenApiRouteInfo) -> OpenApiRouteInfo = { it }) :
    Path<T> by delegate,
    Documentable<OpenApiRouteInfo>,
    Kind<ForOpenApiPath, T> {

    override fun document(doc: OpenApiRouteInfo): OpenApiRouteInfo =
        documenter(doc)

    override fun toString(): String = delegate.toString()
}
