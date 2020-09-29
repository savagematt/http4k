package org.http4k.typesafe.routing.messages

import com.natpryce.Failure
import com.natpryce.Success
import com.natpryce.flatMap
import org.http4k.core.HttpMessage
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError

class RequiredLens<M : HttpMessage, T>(
    val optional: MessageLens<M, T?, *>,
    onFailure: (() -> RoutingError)? = null
) : SimpleLens<M, T> {
    val onFailure = onFailure ?: { RoutingError.RouteFailed("$optional is required", Response(Status.BAD_REQUEST)) }
    override fun get(from: M) =
        optional.get(from).let { result ->
            result.flatMap {
                when (it) {
                    null -> Failure(onFailure())
                    else -> Success(it)
                }
            }
        }

    override fun set(into: M, value: T) =
        optional.set(into, value)

    override fun toString() = "required: $optional"
}