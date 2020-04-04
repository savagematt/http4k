package org.http4k.typesafe.routing.messages

import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.data.Tuple6
import org.http4k.typesafe.data.tuple
import org.http4k.typesafe.routing.MessageLens

class Tuple6Lens<M : HttpMessage, A, B, C, D, E, F>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>,
    val d: MessageLens<M, D>,
    val e: MessageLens<M, E>,
    val f: MessageLens<M, F>
) : SimpleLens<M, Tuple6<A, B, C, D, E, F>> {

    infix fun <T> and(next: MessageLens<M, T>) = Tuple7Lens(a, b, c, d, e, f, next)

    override fun get(from: M) =
        a.get(from).flatMap { a ->
            b.get(from).flatMap { b ->
                c.get(from).flatMap { c ->
                    d.get(from).flatMap { d ->
                        e.get(from).flatMap { e ->
                            f.get(from).map { f ->
                                tuple(a, b, c, d, e, f)
                            }
                        }
                    }
                }
            }
        }

    override fun set(into: M, value: Tuple6<A, B, C, D, E, F>) =
        a.set(into, value.a).flatMap {
            b.set(it, value.b).flatMap {
                c.set(it, value.c).flatMap {
                    d.set(it, value.d).flatMap {
                        e.set(it, value.e).flatMap {
                            f.set(it, value.f)
                        }
                    }
                }
            }
        }

}
