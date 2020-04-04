package org.http4k.typesafe.routing.messages.tuples

import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.data.Tuple4
import org.http4k.typesafe.data.tuple
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.messages.SimpleLens

class Tuple4Lens<M : HttpMessage, A, B, C, D>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>,
    val d: MessageLens<M, D>
) : SimpleLens<M, Tuple4<A, B, C, D>> {
    infix fun <T> and(next: MessageLens<M, T>) = Tuple5Lens(a, b, c, d, next)

    override fun get(from: M) =
        a.get(from).flatMap { a ->
            b.get(from).flatMap { b ->
                c.get(from).flatMap { c ->
                    d.get(from).map { d ->
                        tuple(a, b, c, d)
                    }
                }
            }
        }

    override fun set(into: M, value: Tuple4<A, B, C, D>) =
        a.set(into, value.a).flatMap {
            b.set(it, value.b).flatMap {
                c.set(it, value.c).flatMap {
                    d.set(it, value.d)
                }
            }
        }

}
