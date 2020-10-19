package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.routing.fold
import org.http4k.util.data.Tuple8
import org.http4k.util.data.tuple

data class Path8<A, B, C, D, E, F, G, H, Docs>(
    val a: Path<A, Docs>,
    val b: Path<B, Docs>,
    val c: Path<C, Docs>,
    val d: Path<D, Docs>,
    val e: Path<E, Docs>,
    val f: Path<F, Docs>,
    val g: Path<G, Docs>,
    val h: Path<H, Docs>
) : Path<Tuple8<A, B, C, D, E, F, G, H>, Docs> {
    operator fun <T> div(next: Path<T, Docs>) = Path9(a, b, c, d, e, f, g, h, next)

    override fun get(from: String): PathResult<Tuple8<A, B, C, D, E, F, G, H>> =
        a.get(from).flatMap { a ->
            b.get(a.remaining).flatMap { b ->
                c.get(b.remaining).flatMap { c ->
                    d.get(c.remaining).flatMap { d ->
                        e.get(d.remaining).flatMap { e ->
                            f.get(e.remaining).flatMap { f ->
                                g.get(f.remaining).flatMap { g ->
                                    h.get(g.remaining).flatMap { h ->
                                        matchSuccess(tuple(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value), h.remaining)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun set(into: String, value: Tuple8<A, B, C, D, E, F, G, H>): String =
        h.set(g.set(f.set(e.set(d.set(c.set(b.set(a.set(into, value.a), value.b), value.c), value.d), value.e), value.f), value.g), value.h)

    override fun toString() = joinPaths(a, b, c, d, e, f, g, h)

    override fun document(doc: Docs) = fold(doc, a, b, c, d, e, f, g, h)
}