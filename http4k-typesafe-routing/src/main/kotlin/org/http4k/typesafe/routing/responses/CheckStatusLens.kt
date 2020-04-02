package org.http4k.typesafe.routing.responses

import com.natpryce.Result
import com.natpryce.Success
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.SimpleLens

class CheckStatusLens(val status: Status) : SimpleLens<Response,Unit> {
    override fun get(from: Response): Result<Unit, RoutingError> =
        when (from.status) {
            status -> Success(Unit)
            else -> RoutingError.wrongRoute("Status was not $status")
        }

    override fun set(into: Response, value: Unit): Result<Response, RoutingError> =
        Success(into.status(status))
}