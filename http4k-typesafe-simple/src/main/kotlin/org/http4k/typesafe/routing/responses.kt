package org.http4k.typesafe.routing

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.routing.responses.CheckStatusLens
import org.http4k.typesafe.routing.responses.StatusLens

infix fun <T> Status.with(rest: MessageLens<Response, T>) =
    CheckStatusLens(this, rest)

fun status() =
    StatusLens()
