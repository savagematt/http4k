package org.http4k.typesafe.routing.messages.tuples

import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.data.Tuple9
import org.http4k.typesafe.data.tuple
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.messages.SimpleLens

class Tuple9Lens<M : HttpMessage, A, B, C, D, E, F, G, H, I>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>,
    val d: MessageLens<M, D>,
    val e: MessageLens<M, E>,
    val f: MessageLens<M, F>,
    val g: MessageLens<M, G>,
    val h: MessageLens<M, H>,
    val i: MessageLens<M, I>
) : SimpleLens<M, Tuple9<A, B, C, D, E, F, G, H, I>> {

    infix fun <T> and(next: MessageLens<M, T>) =
        Tuple10Lens(a, b, c, d, e, f, g, h, i, next)

    override fun get(from: M) =
        a.get(from).flatMap { a ->
            b.get(from).flatMap { b ->
                c.get(from).flatMap { c ->
                    d.get(from).flatMap { d ->
                        e.get(from).flatMap { e ->
                            f.get(from).flatMap { f ->
                                g.get(from).flatMap { g ->
                                    h.get(from).flatMap { h ->
                                        i.get(from).map { i ->
                                            tuple(a, b, c, d, e, f, g, h, i)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun set(into: M, value: Tuple9<A, B, C, D, E, F, G, H, I>) =
        a.set(into, value.a).flatMap {
            b.set(it, value.b).flatMap {
                c.set(it, value.c).flatMap {
                    d.set(it, value.d).flatMap {
                        e.set(it, value.e).flatMap {
                            f.set(it, value.f).flatMap {
                                g.set(it, value.g).flatMap {
                                    h.set(it, value.h).flatMap {
                                        i.set(it, value.i)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun toString() = listOf(a, b, c, d, e, f, g, h, i).joinToString("; ")
}