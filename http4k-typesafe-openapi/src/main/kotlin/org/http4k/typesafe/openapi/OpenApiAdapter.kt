package org.http4k.typesafe.openapi

import org.http4k.core.HttpMessage
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.typesafe.routing.Documentable
import org.http4k.typesafe.routing.MessageLens

/**
 * Adapts any [MessageLens] into an [OpenApiLens].
 *
 * If [original] is already an [OpenApiLens], this can be used to
 * override the [document] behaviour.
 *
 * Leaves behaviour of toString() untouched.
 */
open class OpenApiAdapter<M : HttpMessage, T>(
    private val original: MessageLens<M, T, *>,
    private val documenter: ((OpenApiRouteDocs) -> OpenApiRouteDocs)? = null) :
    OpenApiLens<M, T> {

    override fun document(doc: OpenApiRouteDocs): OpenApiRouteDocs =
        when (documenter) {
            null -> doc
            else -> documenter.invoke(doc)
        }

    override fun toString() = original.toString()

    infix fun openapi(more: OpenApiRouteInfoDsl.() -> Unit): OpenApiLens<M, T> =
        OpenApiAdapter(this) { OpenApiRouteInfoDsl(this.document(it)).also(more).build() }

    override fun get(from: M) = original.get(from)

    override fun set(into: M, value: T) = original.set(into, value)
}

/*
Adapting existing lenses
------------------------------------------------------------------
 */

infix fun <M : HttpMessage, T> MessageLens<M, T, *>.openapi(
    docs: (OpenApiRouteInfoDsl.() -> Unit)): OpenApiLens<M, T> =
    this documentation { OpenApiRouteInfoDsl(it).also(docs).build() }

infix fun <M : HttpMessage, T> MessageLens<M, T, *>.documentation(
    docs: (OpenApiRouteDocs) -> OpenApiRouteDocs): OpenApiLens<M, T> =
    OpenApiAdapter(this, docs)

infix fun <M : HttpMessage, T> MessageLens<M, T, *>.documentation(
    docs: Documentable<OpenApiRouteDocs>): OpenApiLens<M, T> =
    OpenApiAdapter(this) { docs.document(it) }