package org.http4k.typesafe.openapi

import org.http4k.core.HttpMessage
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.typesafe.routing.MessageLens

typealias OpenApiLens<M, T> = MessageLens<M, T, OpenApiRouteDocs>

/**
 * Adds additional documentation to an existing open api lens
 */
infix fun <M : HttpMessage, T> OpenApiLens<M, T>.openapi(more: OpenApiRouteInfoDsl.() -> Unit): OpenApiLens<M, T> =
    OpenApiAdapter(this) { OpenApiRouteInfoDsl(this.document(it)).also(more).build() }
