package org.http4k.typesafe.routing.messages

import com.natpryce.Result
import com.natpryce.flatMap
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError

class MappedLens<M : HttpMessage, A, B>(
    val initial: MessageLens<M, A>,
    val getter: (A) -> Result<B, RoutingError>,
    val setter: (B) -> A
) : SimpleLens<M, B> {
    override fun get(from: M): Result<B, RoutingError> =
        initial.get(from).flatMap {
            getter(it)
        }

    override fun set(into: M, value: B): Result<M, RoutingError> =
        initial.set(into, setter(value))

    override fun toString(): String = "mapped($initial)"
}