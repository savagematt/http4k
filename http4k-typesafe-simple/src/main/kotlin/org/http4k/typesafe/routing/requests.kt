package org.http4k.typesafe.routing

import com.natpryce.Result
import org.http4k.core.Credentials
import org.http4k.core.HttpMessage
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.format.Json
import org.http4k.typesafe.routing.messages.body.JsonLens
import org.http4k.typesafe.routing.requests.CheckMethodLens
import org.http4k.typesafe.routing.requests.MethodLens
import org.http4k.typesafe.routing.requests.PathLens
import org.http4k.typesafe.routing.requests.auth.BasicAuthClientLens
import org.http4k.typesafe.routing.requests.auth.BasicAuthServerLens
import org.http4k.typesafe.routing.requests.paths.Literal
import org.http4k.typesafe.routing.requests.paths.Path

fun <T> path(path: Path<T>) =
    PathLens(path)

infix fun <T> Method.bind(
    path: Path<T>) =
    method(this, path(path))

infix fun Method.bind(
    path: String) =
    this.bind(Literal(path))

fun <T> method(method: Method, rest: MessageLens<Request, T>) =
    CheckMethodLens(method, rest)

fun method() =
    MethodLens()

fun basicAuthServer(validator: (Credentials) -> Result<String, RoutingError>) =
    BasicAuthServerLens(validator)

fun basicAuthClient(provider: (String) -> Credentials?) =
    BasicAuthClientLens(provider)

fun <M : HttpMessage, NODE : Any> json(json: Json<NODE>) = JsonLens<M, NODE>(json)