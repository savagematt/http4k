package org.http4k.typesafe.routing.messages

import com.natpryce.Failure
import com.natpryce.Success
import com.natpryce.flatMap
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError

/**
 * Maps an
 */
class RequiredLens<M : HttpMessage, T>(
    val opt: MessageLens<M, T?>,
    val onFailure: () -> RoutingError
) : SimpleLens<M, T> {
    override fun get(from: M) =
        opt.get(from).let { result ->
            result.flatMap {
                when (it) {
                    null -> Failure(onFailure())
                    else -> Success(it)
                }
            }
        }

    override fun set(into: M, value: T) =
        opt.set(into, value)

    override fun toString() = "required: $opt"
}