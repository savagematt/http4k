package org.http4k.typesafe.openapi.routing

import com.natpryce.Result
import org.http4k.core.Credentials
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.typesafe.functional.Kind
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.openapi.ForOpenApiLens
import org.http4k.typesafe.openapi.ForOpenApiPath
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.documentation
import org.http4k.typesafe.openapi.routing.OpenApiPaths.literal
import org.http4k.typesafe.openapi.fix
import org.http4k.typesafe.openapi.fold
import org.http4k.typesafe.openapi.messages.basicAuth
import org.http4k.typesafe.openapi.openapi
import org.http4k.typesafe.routing.RequestRouting
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.requests.CheckMethodLens
import org.http4k.typesafe.routing.requests.MethodLens
import org.http4k.typesafe.routing.requests.PathLens
import org.http4k.typesafe.routing.requests.auth.BasicAuthClientLens
import org.http4k.typesafe.routing.requests.auth.BasicAuthServerLens

object OpenApiRequestRouting :
    OpenApiMessageRouting<Request>(Request::class),
    RequestRouting<ForOpenApiLens, ForOpenApiPath> {

    override fun <T> path(path: Kind<ForOpenApiPath, T>): OpenApiLens<Request, T> {
        val p = path.fix()
        return PathLens(p) documentation p
    }

    override fun <T> Method.bind(
        path: Kind<ForOpenApiPath, T>) =
        method(
            this,
            PathLens(path.fix()) documentation fold(path.fix()))

    override fun Method.bind(
        path: String): Kind2<ForOpenApiLens, Request, Unit> {
        return method(this, path(literal(path)))
    }

    override fun <T> method(method: Method, rest: Kind2<ForOpenApiLens, Request, T>) =
        CheckMethodLens(method, rest.fix()) documentation rest.fix() openapi {
            route {
                operation {
                    this@route.method = method
                }
            }
        }

    override fun method() =
        MethodLens().openapi(null ?: {})

    override fun basicAuthServer(validator: (Credentials) -> Result<String, RoutingError>) =
        BasicAuthServerLens(validator) openapi basicAuth()

    override fun basicAuthClient(provider: (String) -> Credentials?) =
        BasicAuthClientLens(provider) openapi basicAuth()
}

