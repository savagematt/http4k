package org.http4k.typesafe.routing.messages

import com.natpryce.Result
import com.natpryce.flatMap
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError

interface LensValueMapper<A, B> {
    fun get(value: A): Result<B, RoutingError>
    fun set(value: B): A
}

fun <A, B> mapper(getter: (A) -> Result<B, RoutingError>,
                  setter: (B) -> A
): LensValueMapper<A, B> {
    return object : LensValueMapper<A, B> {
        override fun get(value: A): Result<B, RoutingError> {
            return getter(value)
        }

        override fun set(value: B): A {
            return setter(value)
        }
    }
}

class MappedLens<M : HttpMessage, A, B>(
    val initial: MessageLens<M, A, *>,
    val mapper: LensValueMapper<A, B>
) : SimpleLens<M, B> {

    override fun get(from: M): Result<B, RoutingError> =
        initial.get(from).flatMap {
            mapper.get(it)
        }

    override fun set(into: M, value: B): Result<M, RoutingError> =
        initial.set(into, mapper.set(value))

    override fun toString(): String = "mapped($initial)"
}