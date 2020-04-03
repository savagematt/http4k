package org.http4k.typesafe.routing

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.responses.CheckStatusLens
import org.http4k.typesafe.routing.responses.StatusLens
import org.http4k.typesafe.routing.simple.ForSimpleLens
import org.http4k.typesafe.routing.simple.fix

object SimpleResponseRouting :
    SimpleMessageRouting<Response>(),
    ResponseRouting<ForSimpleServerRoute, ForSimpleRoute, ForSimpleLens> {
    override fun <T> status(status: Status, rest: Kind2<ForSimpleLens, Response, T>) =
        CheckStatusLens(status, rest.fix())

    override fun status() =
        StatusLens()
}