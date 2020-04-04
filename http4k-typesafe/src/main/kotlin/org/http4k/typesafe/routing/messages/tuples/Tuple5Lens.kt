package org.http4k.typesafe.routing.messages.tuples

import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.data.Tuple5
import org.http4k.typesafe.data.tuple
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.messages.SimpleLens

class Tuple5Lens<M : HttpMessage, A, B, C, D, E>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>,
    val d: MessageLens<M, D>,
    val e: MessageLens<M, E>
) : SimpleLens<M, Tuple5<A, B, C, D, E>> {
    infix fun <T> and(next: MessageLens<M, T>) = Tuple6Lens(a, b, c, d, e, next)

    override fun get(from: M) =
        a.get(from).flatMap { a ->
            b.get(from).flatMap { b ->
                c.get(from).flatMap { c ->
                    d.get(from).flatMap { d ->
                        e.get(from).map { e ->
                            tuple(a, b, c, d, e)
                        }
                    }
                }
            }
        }

    override fun set(into: M, value: Tuple5<A, B, C, D, E>) =
        a.set(into, value.a).flatMap {
            b.set(it, value.b).flatMap {
                c.set(it, value.c).flatMap {
                    d.set(it, value.d).flatMap {
                        e.set(it, value.e)
                    }
                }
            }
        }

}
