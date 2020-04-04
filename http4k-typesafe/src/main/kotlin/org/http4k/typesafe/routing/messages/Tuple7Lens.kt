package org.http4k.typesafe.routing.messages

import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.data.Tuple7
import org.http4k.typesafe.data.tuple
import org.http4k.typesafe.routing.MessageLens

class Tuple7Lens<M : HttpMessage, A, B, C, D, E, F, G>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>,
    val d: MessageLens<M, D>,
    val e: MessageLens<M, E>,
    val f: MessageLens<M, F>,
    val g: MessageLens<M, G>
) : SimpleLens<M, Tuple7<A, B, C, D, E, F, G>> {

    infix fun <T> and(next: MessageLens<M, T>) = Tuple8Lens(a, b, c, d, e, f, g, next)

    override fun get(from: M) =
        a.get(from).flatMap { a ->
            b.get(from).flatMap { b ->
                c.get(from).flatMap { c ->
                    d.get(from).flatMap { d ->
                        e.get(from).flatMap { e ->
                            f.get(from).flatMap { f ->
                                g.get(from).map { g ->
                                    tuple(a, b, c, d, e, f, g)
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun set(into: M, value: Tuple7<A, B, C, D, E, F, G>) =
        a.set(into, value.a).flatMap {
            b.set(it, value.b).flatMap {
                c.set(it, value.c).flatMap {
                    d.set(it, value.d).flatMap {
                        e.set(it, value.e).flatMap {
                            f.set(it, value.f).flatMap {
                                g.set(it, value.g)
                            }
                        }
                    }
                }
            }
        }

}
