package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.routing.fold
import org.http4k.util.data.Tuple9
import org.http4k.util.data.tuple

data class Path9<A, B, C, D, E, F, G, H, I, Docs>(
    val a: Path<A, Docs>,
    val b: Path<B, Docs>,
    val c: Path<C, Docs>,
    val d: Path<D, Docs>,
    val e: Path<E, Docs>,
    val f: Path<F, Docs>,
    val g: Path<G, Docs>,
    val h: Path<H, Docs>,
    val i: Path<I, Docs>
) : Path<Tuple9<A, B, C, D, E, F, G, H, I>, Docs> {
    operator fun <T> div(next: Path<T, Docs>) = Path10(a, b, c, d, e, f, g, h, i, next)

    override fun get(from: String): PathResult<Tuple9<A, B, C, D, E, F, G, H, I>> =
        a.get(from).flatMap { a ->
            b.get(a.remaining).flatMap { b ->
                c.get(b.remaining).flatMap { c ->
                    d.get(c.remaining).flatMap { d ->
                        e.get(d.remaining).flatMap { e ->
                            f.get(e.remaining).flatMap { f ->
                                g.get(f.remaining).flatMap { g ->
                                    h.get(g.remaining).flatMap { h ->
                                        i.get(h.remaining).flatMap { i ->
                                            matchSuccess(tuple(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value), i.remaining)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun set(into: String, value: Tuple9<A, B, C, D, E, F, G, H, I>): String =
        i.set(h.set(g.set(f.set(e.set(d.set(c.set(b.set(a.set(into, value.a), value.b), value.c), value.d), value.e), value.f), value.g), value.h), value.i)

    override fun toString() = joinPaths(a, b, c, d, e, f, g, h, i)

    override fun document(doc: Docs) = fold(doc, a, b, c, d, e, f, g, h, i)
}