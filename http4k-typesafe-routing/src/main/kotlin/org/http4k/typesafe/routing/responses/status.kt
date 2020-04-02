package org.http4k.typesafe.routing.responses

import com.natpryce.Result
import com.natpryce.Success
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.routing.ResponseLens
import org.http4k.typesafe.routing.RoutingError


class CheckButDoNotCaptureStatusLens(val status: Status) : ResponseLens<Unit> {
    override fun get(from: Response): Result<Unit, RoutingError> =
        when (from.status) {
            status -> Success(Unit)
            else -> RoutingError.wrongRoute("Status was not $status")
        }

    override fun set(into: Response, value: Unit): Result<Response, RoutingError> =
        Success(into.status(status))
}

fun status(status: Status) = CheckButDoNotCaptureStatusLens(status)

class CaptureStatusLens : ResponseLens<Status> {
    override fun get(from: Response): Result<Status, RoutingError> =
        Success(from.status)

    override fun set(into: Response, value: Status): Result<Response, RoutingError> =
        Success(into.status(value))
}

fun captureStatus() = CaptureStatusLens()

