package org.http4k.typesafe.routing

import com.natpryce.Result
import org.http4k.core.Credentials
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.typesafe.functional.Kind
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.messages.ForSimpleLens
import org.http4k.typesafe.routing.messages.fix
import org.http4k.typesafe.routing.requests.CheckMethodLens
import org.http4k.typesafe.routing.requests.MethodLens
import org.http4k.typesafe.routing.requests.PathLens
import org.http4k.typesafe.routing.requests.auth.BasicAuthClientLens
import org.http4k.typesafe.routing.requests.auth.BasicAuthServerLens
import org.http4k.typesafe.routing.requests.paths.ForSimplePath
import org.http4k.typesafe.routing.requests.paths.Literal
import org.http4k.typesafe.routing.requests.paths.fix

object SimpleRequestRouting :
    SimpleMessageRouting<Request>(),
    RequestRouting<ForSimpleLens, ForSimplePath> {

    override fun <T> path(path: Kind<ForSimplePath, T>) =
        PathLens(path.fix())

    override fun <T> Method.bind(
        path: Kind<ForSimplePath, T>) =
        method(this, path(path))

    override fun Method.bind(
        path: String) =
        this.bind(Literal(path))

    override fun <T> method(method: Method, rest: Kind2<ForSimpleLens, Request, T>) =
        CheckMethodLens(method, rest.fix())

    override fun method() =
        MethodLens()

    override fun basicAuthServer(validator: (Credentials) -> Result<String, RoutingError>) =
        BasicAuthServerLens(validator)

    override fun basicAuthClient(provider: (String) -> Credentials?) =
        BasicAuthClientLens(provider)
}