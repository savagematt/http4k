package org.http4k.typesafe.openapi

import org.http4k.core.HttpMessage
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.typesafe.routing.MessageLens

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
class ForOpenApiLens private constructor() {
    companion object
}

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, T>.fix() = this as OpenApiLens<M, T>

class OpenApiLens<M : HttpMessage, T>(
    private val original: MessageLens<M, T>,
    private val documenter: ((OpenApiRouteInfo) -> OpenApiRouteInfo)? = null) :
    MessageLens<M, T> by original,
    Documentable<OpenApiRouteInfo>,
    Kind2<ForOpenApiLens, M, T> {

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
Adapting higher kinds
------------------------------------------------------------------
 */
infix fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, T>.openapi(
    docs: (OpenApiRouteInfoDsl.() -> Unit)) =
    this.fix() openapi docs

infix fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, T>.documentation(
    docs: (OpenApiRouteInfo) -> OpenApiRouteInfo) =
    this.fix() documentation docs

infix fun <M : HttpMessage, T> Kind2<ForOpenApiLens, M, T>.documentation(
    docs: Documentable<OpenApiRouteInfo>) =
    this.fix() documentation docs

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