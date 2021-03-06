package org.http4k.typesafe.routing.responses

import com.natpryce.Result
import com.natpryce.map
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.wrongRoute
import org.http4k.typesafe.routing.messages.SimpleLens

class CheckStatusLens<T>(
    val status: Status,
    val rest: MessageLens<Response, T, *>) : SimpleLens<Response, T> {

    override fun get(from: Response): Result<T, RoutingError> =
        when (from.status) {
            status -> rest.get(from)
            else -> wrongRoute("Status was ${from.status.code} (not ${status.code})")
        }

    override fun set(into: Response, value: T): Result<Response, RoutingError> =
        rest.set(into, value).map { it.status(status) }

    override fun toString() = "Status ${status.code}"
}