package org.http4k.typesafe.openapi.routing

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.openapi.ForOpenApiLens
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.documentation
import org.http4k.typesafe.openapi.fix
import org.http4k.typesafe.openapi.openapi
import org.http4k.typesafe.routing.ResponseRouting
import org.http4k.typesafe.routing.responses.CheckStatusLens
import org.http4k.typesafe.routing.responses.StatusLens

object OpenApiResponseRouting : OpenApiMessageRouting<Response>(Response::class), ResponseRouting<ForOpenApiLens> {
    override infix fun <T> Status.with(rest: Kind2<ForOpenApiLens, Response, T>): OpenApiLens<Response, T> {
        val restLens = rest.fix()
        return CheckStatusLens(this, restLens) documentation restLens openapi {
            route {
                operation {
                    responses {
                        if (default.value == null) {
                            TODO("Cannot yet set status code on OpenApiResponses with no default response")
                        } else {
                            responses.byStatus += (this@with to responses.default.build()!!)
                            default.value = null
                        }
                    }
                }
            }

        }
    }

    override fun status() =
        StatusLens().openapi(null ?: {})
}