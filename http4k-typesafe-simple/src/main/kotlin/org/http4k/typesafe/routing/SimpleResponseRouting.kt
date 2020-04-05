package org.http4k.typesafe.routing

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.messages.ForSimpleLens
import org.http4k.typesafe.routing.messages.fix
import org.http4k.typesafe.routing.responses.CheckStatusLens
import org.http4k.typesafe.routing.responses.StatusLens

object SimpleResponseRouting :
    SimpleMessageRouting<Response>(),
    ResponseRouting<ForSimpleLens> {
    override infix fun <T> Status.with(rest: Kind2<ForSimpleLens, Response, T>) =
        CheckStatusLens(this, rest.fix())

    override fun status() =
        StatusLens()
}