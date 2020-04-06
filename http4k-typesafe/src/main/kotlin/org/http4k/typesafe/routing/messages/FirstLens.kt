package org.http4k.typesafe.routing.messages

import com.natpryce.Failure
import com.natpryce.Result
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError

class FirstLens<M : HttpMessage, T>(
    val lenses: List<MessageLens<M, T>>
) : SimpleLens<M, T> {
    init {
        if (lenses.isEmpty())
            throw IllegalArgumentException("${this::class.simpleName} requires at least one lens")
    }

    override fun get(from: M): Result<T, RoutingError> =
        lenses.asSequence()
            .map { it.get(from) }
            .takeWhile { it is Failure }
            .last()

    override fun set(into: M, value: T): Result<M, RoutingError> =
        lenses.asSequence()
            .map { it.set(into, value) }
            .takeWhile { it is Failure }
            .last()

    override fun toString() = lenses.joinToString(" | ")
}