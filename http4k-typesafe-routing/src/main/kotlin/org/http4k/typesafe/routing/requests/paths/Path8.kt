package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.data.Tuple8
import org.http4k.typesafe.data.tuple
import org.http4k.typesafe.routing.requests.paths.MatchResult.Companion.matchSuccess

data class Path8<A, B, C, D, E, F, G, H>(
    val a: Path<A>,
    val b: Path<B>,
    val c: Path<C>,
    val d: Path<D>,
    val e: Path<E>,
    val f: Path<F>,
    val g: Path<G>,
    val h: Path<H>
) : Path<Tuple8<A, B, C, D, E, F, G, H>> {
    operator fun <T> div(next: Path<T>) = Path9(a, b, c, d, e, f, g, h, next)

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
}