package org.http4k.typesafe.openapi

import org.http4k.core.HttpMessage
import org.http4k.openapi.OpenApiRouteInfo
import org.http4k.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.typesafe.routing.MessageLens
import org.http4k.util.Documentable

/**
 * Adapts any [MessageLens] into an [OpenApiLens].
 *
 * If [original] is already an [OpenApiLens], this can be used to
 * override the [document] behaviour.
 *
 * Leaves behaviour of toString() untouched.
 */
class OpenApiAdapter<M : HttpMessage, T>(
    private val original: MessageLens<M, T>,
    private val documenter: ((OpenApiRouteInfo) -> OpenApiRouteInfo)? = null) :
    OpenApiLens<M, T>,
    MessageLens<M, T> by original {

    override fun document(doc: OpenApiRouteInfo): OpenApiRouteInfo =
        when (documenter) {
            null -> doc
            else -> documenter.invoke(doc)
        }

    override fun toString() = original.toString()

    infix fun openapi(more: OpenApiRouteInfoDsl.() -> Unit): OpenApiLens<M, T> =
        OpenApiAdapter(this) { OpenApiRouteInfoDsl(this.document(it)).also(more).build() }
}

/*
Adapting existing lenses
------------------------------------------------------------------
 */

infix fun <M : HttpMessage, T> MessageLens<M, T>.openapi(
    docs: (OpenApiRouteInfoDsl.() -> Unit)) =
    this documentation { OpenApiRouteInfoDsl(it).also(docs).build() }

infix fun <M : HttpMessage, T> MessageLens<M, T>.documentation(
    docs: (OpenApiRouteInfo) -> OpenApiRouteInfo) =
    OpenApiAdapter(this, docs)

infix fun <M : HttpMessage, T> MessageLens<M, T>.documentation(
    docs: Documentable<OpenApiRouteInfo>) =
    OpenApiAdapter(this) { docs.document(it) }