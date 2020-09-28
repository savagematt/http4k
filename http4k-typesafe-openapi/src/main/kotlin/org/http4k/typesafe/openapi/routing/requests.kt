package org.http4k.typesafe.openapi.routing

import com.natpryce.Result
import org.http4k.core.Credentials
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.OpenApiPath
import org.http4k.typesafe.openapi.documentable.basicAuth
import org.http4k.typesafe.openapi.documentation
import org.http4k.typesafe.openapi.openapi
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.requests.CheckMethodLens
import org.http4k.typesafe.routing.requests.MethodLens
import org.http4k.typesafe.routing.requests.PathLens
import org.http4k.typesafe.routing.requests.auth.BasicAuthClientLens
import org.http4k.typesafe.routing.requests.auth.BasicAuthServerLens
import org.http4k.util.fold

fun <T> path(path: OpenApiPath<T>): OpenApiLens<Request, T> {
    val p = path
    return PathLens(p) documentation p
}

infix fun <T> Method.at(
    path: OpenApiPath<T>) =
    method(
        this,
        PathLens(path) documentation fold(path))

infix fun Method.at(
    path: String): OpenApiLens<Request, Unit> {
    return method(this, path(literal(path)))
}

fun <T> method(method: Method, rest: OpenApiLens<Request, T>) =
    CheckMethodLens(method, rest) documentation rest openapi {
        route {
            operation {
                this@route.method = method
            }
        }
    }

fun method() =
    MethodLens().openapi(null ?: {})

fun basicAuthServer(validator: (Credentials) -> Result<String, RoutingError>) =
    BasicAuthServerLens(validator) openapi basicAuth()

fun basicAuthClient(provider: (String) -> Credentials?) =
    BasicAuthClientLens(provider) openapi basicAuth()

