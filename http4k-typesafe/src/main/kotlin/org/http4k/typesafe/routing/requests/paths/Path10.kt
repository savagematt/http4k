package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.data.Tuple10
import org.http4k.typesafe.data.tuple

data class Path10<A, B, C, D, E, F, G, H, I, J>(
    val a: Path<A>,
    val b: Path<B>,
    val c: Path<C>,
    val d: Path<D>,
    val e: Path<E>,
    val f: Path<F>,
    val g: Path<G>,
    val h: Path<H>,
    val i: Path<I>,
    val j: Path<J>
) : Path<Tuple10<A, B, C, D, E, F, G, H, I, J>> {
    override fun get(from: String): PathResult<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
        a.get(from).flatMap { a ->
            b.get(a.remaining).flatMap { b ->
                c.get(b.remaining).flatMap { c ->
                    d.get(c.remaining).flatMap { d ->
                        e.get(d.remaining).flatMap { e ->
                            f.get(e.remaining).flatMap { f ->
                                g.get(f.remaining).flatMap { g ->
                                    h.get(g.remaining).flatMap { h ->
                                        i.get(h.remaining).flatMap { i ->
                                            j.get(i.remaining).flatMap { j ->
                                                matchSuccess(tuple(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value), j.remaining)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    override fun set(into: String, value: Tuple10<A, B, C, D, E, F, G, H, I, J>): String =
        j.set(i.set(h.set(g.set(f.set(e.set(d.set(c.set(b.set(a.set(into, value.a), value.b), value.c), value.d), value.e), value.f), value.g), value.h), value.i), value.j)
}