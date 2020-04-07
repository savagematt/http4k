package org.http4k.typesafe.routing.messages.tuples

import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.util.data.Tuple3
import org.http4k.util.data.tuple
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.messages.SimpleLens

class Tuple3Lens<M : HttpMessage, A, B, C>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>
) : SimpleLens<M, Tuple3<A, B, C>> {
    infix fun <T> and(next: MessageLens<M, T>) = Tuple4Lens(a, b, c, next)

    override fun get(from: M) =
        a.get(from).flatMap { a ->
            b.get(from).flatMap { b ->
                c.get(from).map { c ->
                    tuple(a, b, c)
                }
            }
        }

    override fun set(into: M, value: Tuple3<A, B, C>) =
        a.set(into, value.a).flatMap {
            b.set(it, value.b).flatMap {
                c.set(it, value.c)
            }
        }

    override fun toString() = listOf(a, b, c).joinToString("; ")
}
