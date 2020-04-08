package org.http4k.typesafe.openapi

import org.http4k.core.HttpMessage
import org.http4k.openapi.OpenApiRouteInfo
import org.http4k.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.typesafe.routing.MessageLens
import org.http4k.util.Documentable

class OpenApiLens<M : HttpMessage, T>(
    private val original: MessageLens<M, T>,
    private val documenter: ((OpenApiRouteInfo) -> OpenApiRouteInfo)? = null) :
    MessageLens<M, T> by original,
    Documentable<OpenApiRouteInfo> {

    override fun document(doc: OpenApiRouteInfo): OpenApiRouteInfo =
        when (documenter) {
            null -> doc
            else -> documenter.invoke(doc)
        }

    override fun toString() = original.toString()

    infix fun documentation(more: ((OpenApiRouteInfo) -> OpenApiRouteInfo)) =
        OpenApiLens(this) { more(this.document(it)) }

    infix fun documentation(more: Documentable<OpenApiRouteInfo>) =
        OpenApiLens(this) { more.document(this.document(it)) }

    infix fun openapi(more: OpenApiRouteInfoDsl.() -> Unit) =
        OpenApiLens(this) { OpenApiRouteInfoDsl(this.document(it)).also(more).build() }
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
    OpenApiLens(this, docs)

infix fun <M : HttpMessage, T> MessageLens<M, T>.documentation(
    docs: Documentable<OpenApiRouteInfo>) =
    OpenApiLens(this) { docs.document(it) }