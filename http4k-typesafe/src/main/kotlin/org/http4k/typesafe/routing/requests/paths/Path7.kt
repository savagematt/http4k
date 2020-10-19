package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.routing.fold
import org.http4k.util.data.Tuple7
import org.http4k.util.data.tuple

data class Path7<A, B, C, D, E, F, G, Docs>(
    val a: Path<A, Docs>,
    val b: Path<B, Docs>,
    val c: Path<C, Docs>,
    val d: Path<D, Docs>,
    val e: Path<E, Docs>,
    val f: Path<F, Docs>,
    val g: Path<G, Docs>
) : Path<Tuple7<A, B, C, D, E, F, G>, Docs> {
    operator fun <T> div(next: Path<T, Docs>) = Path8(a, b, c, d, e, f, g, next)

    override fun get(from: String): PathResult<Tuple7<A, B, C, D, E, F, G>> =
        a.get(from).flatMap { a ->
            b.get(a.remaining).flatMap { b ->
                c.get(b.remaining).flatMap { c ->
                    d.get(c.remaining).flatMap { d ->
                        e.get(d.remaining).flatMap { e ->
                            f.get(e.remaining).flatMap { f ->
                                g.get(f.remaining).flatMap { g ->
                                    matchSuccess(tuple(a.value, b.value, c.value, d.value, e.value, f.value, g.value), g.remaining)
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun set(into: String, value: Tuple7<A, B, C, D, E, F, G>): String =
        g.set(f.set(e.set(d.set(c.set(b.set(a.set(into, value.a), value.b), value.c), value.d), value.e), value.f), value.g)

    override fun toString() = joinPaths(a, b, c, d, e, f, g)

    override fun document(doc: Docs) = fold(doc, a, b, c, d, e, f, g)
}