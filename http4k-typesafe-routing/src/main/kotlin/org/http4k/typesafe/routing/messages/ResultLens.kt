package org.http4k.typesafe.routing.messages

import com.natpryce.*
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError

class ResultMessageLens<M : HttpMessage, T, E>(
    val success: MessageLens<M, T>,
    val failure: MessageLens<M, E>) : MessageLens<M, Result<T, E>> {

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

/**
 * TODO: should success or failure have priority in get()?
 *
 * If the success lens matches a message, return the result.
 *
 * Otherwise, return the result of failure (which may be a RoutingError)
 *
 * Commonly used to model routes which might return BAD_REQUEST, for example:
 *
 * ```
 * val lens: ResultMessageLens<Response, String, String> =
 *   result(
 *     status(OK) but text(),
 *     status(BAD_REQUEST) but text())
 * ```
 *
 * This might initially seem confusing because it returns a Result inside a
 * Result. You'll probably find you never need to think about that in
 * your own code.
 */
fun <M : HttpMessage, T, E> result(success: MessageLens<M, T>, failure: MessageLens<M, E>) =
    ResultMessageLens(success, failure)