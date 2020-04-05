package org.http4k.typesafe.openapi

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.data.plus
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.ResponseRouting
import org.http4k.typesafe.routing.responses.CheckStatusLens
import org.http4k.typesafe.routing.responses.StatusLens

object OpenApiResponseRouting : OpenApiMessageRouting<Response>(Response::class), ResponseRouting<ForOpenApiLens> {
    override infix fun <T> Status.with(rest: Kind2<ForOpenApiLens, Response, T>): OpenApiLens<Response, T> {
        val restLens = rest.fix()
        return CheckStatusLens(this, restLens).asOpenApi {
            restLens.document(it)
                .responses { responses ->
                    responses.default?.let {
                        responses.copy(
                            default = null,
                            byStatus = responses.byStatus + (this to responses.default))
                    } ?: TODO("Cannot yet set status code on OpenApiResponses with no default response")
                }
        }
    }

    override fun status() =
        StatusLens().asOpenApi()
}