package org.http4k.typesafe.routing.messages

import com.natpryce.*
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.SimpleLens

/**
 * @see [org.http4k.typesafe.routing.Routing.result]
 */
class ResultMessageLens<M : HttpMessage, T, E>(
    val success: MessageLens<M, T>,
    val failure: MessageLens<M, E>) : SimpleLens<M, Result<T, E>> {

    override fun get(from: M): Result<Result<T, E>, RoutingError> =
        success.get(from)
            .map(::Success)
            .flatMapFailure { failure.get(from).map(::Failure) }

    override fun set(into: M, value: Result<T, E>): Result<M, RoutingError> =
        when (value) {
            is Success -> success.set(into, value.value)
            is Failure -> failure.set(into, value.reason)
        }
}
