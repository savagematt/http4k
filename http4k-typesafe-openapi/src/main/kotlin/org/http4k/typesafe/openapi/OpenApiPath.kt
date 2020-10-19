package org.http4k.typesafe.openapi

import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.typesafe.routing.requests.paths.Path

/**
 * Wraps a normal path and adds some openapi documentation
 */
open class OpenApiPath<T>(
    private val delegate: Path<T, *>,
    private val documenter: (OpenApiRouteDocs) -> OpenApiRouteDocs = { it }) :
    Path<T, OpenApiRouteDocs> {

    override fun get(from: String) = delegate.get(from)

    override fun set(into: String, value: T) = delegate.set(into, value)

    override fun document(doc: OpenApiRouteDocs): OpenApiRouteDocs =
        documenter(doc)

    override fun toString(): String = delegate.toString()
}
