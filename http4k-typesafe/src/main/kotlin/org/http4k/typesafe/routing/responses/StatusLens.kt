package org.http4k.typesafe.routing.responses

import com.natpryce.Result
import com.natpryce.Success
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.SimpleLens

/**
 * @see [org.http4k.typesafe.routing.ResponseRouting.status]
 */
class StatusLens : SimpleLens<Response, Status> {
    override fun get(from: Response): Result<Status, RoutingError> =
        Success(from.status)

    override fun set(into: Response, value: Status): Result<Response, RoutingError> =
        Success(into.status(value))

    override fun toString() = "status"
}
