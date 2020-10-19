package org.http4k.typesafe.openapi.routing

import com.natpryce.Result
import com.natpryce.Success
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.openapi.OpenApiResponse
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.openapi.real
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.documentation
import org.http4k.typesafe.openapi.openapi
import org.http4k.typesafe.routing.RoutingError
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

infix fun <T> OpenApiLens<Response, T>.or(status: Status): OpenApiLens<Response, T?> {
    val self = this
    return object : OpenApiLens<Response, T?> {
        override fun get(from: Response): Result<T?, RoutingError> =
            when (from.status) {
                status -> {
                    Success(null)
                }
                else -> {
                    self.get(from)
                }
            }

        override fun set(into: Response, value: T?): Result<Response, RoutingError> =
            when {
                value != null -> {
                    self.set(into, value)
                }
                else -> {
                    Success(into.status(status))
                }
            }

        override fun document(doc: OpenApiRouteDocs): OpenApiRouteDocs = self.document(doc)

    } openapi {
        route {
            operation {
                responses {
                    if (default.value == null) {
                        responses.byStatus += (status to OpenApiResponse(status.description).real())
                    } else {
                        responses.byStatus += (status to responses.default.build()!!)
                        default.value = null
                    }
                }
            }
        }

    }
}

fun status() =
    StatusLens().openapi(null ?: {})
