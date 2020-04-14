package org.http4k.typesafe.openapi.routing

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.openapi.OpenApiResponse
import org.http4k.openapi.real
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.documentation
import org.http4k.typesafe.openapi.openapi
import org.http4k.typesafe.routing.responses.CheckStatusLens
import org.http4k.typesafe.routing.responses.StatusLens

infix fun <T> Status.with(rest: OpenApiLens<Response, T>): OpenApiLens<Response, T> {
    val restLens = rest
    return CheckStatusLens(this, restLens) documentation restLens openapi {
        route {
            operation {
                responses {
                    if (default.value == null) {
                        responses.byStatus += (this@with to OpenApiResponse(this@with.description).real())
                    } else {
                        responses.byStatus += (this@with to responses.default.build()!!)
                        default.value = null
                    }
                }
            }
        }

    }
}

fun status() =
    StatusLens().openapi(null ?: {})
