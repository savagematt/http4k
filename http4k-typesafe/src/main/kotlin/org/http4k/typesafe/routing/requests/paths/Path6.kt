package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.routing.fold
import org.http4k.util.data.Tuple6
import org.http4k.util.data.tuple

data class Path6<A, B, C, D, E, F, Docs>(
    val a: Path<A, Docs>,
    val b: Path<B, Docs>,
    val c: Path<C, Docs>,
    val d: Path<D, Docs>,
    val e: Path<E, Docs>,
    val f: Path<F, Docs>
) : Path<Tuple6<A, B, C, D, E, F>, Docs> {
    operator fun <T> div(next: Path<T, Docs>) = Path7(a, b, c, d, e, f, next)

    override fun get(from: String): PathResult<Tuple6<A, B, C, D, E, F>> =
        a.get(from).flatMap { a ->
            b.get(a.remaining).flatMap { b ->
                c.get(b.remaining).flatMap { c ->
                    d.get(c.remaining).flatMap { d ->
                        e.get(d.remaining).flatMap { e ->
                            f.get(e.remaining).flatMap { f ->
                                matchSuccess(tuple(a.value, b.value, c.value, d.value, e.value, f.value), f.remaining)
                            }
                        }
                    }
                }
            }
        }


    override fun set(into: String, value: Tuple6<A, B, C, D, E, F>): String =
        f.set(e.set(d.set(c.set(b.set(a.set(into, value.a), value.b), value.c), value.d), value.e), value.f)

    override fun toString() = joinPaths(a, b, c, d, e, f)

    override fun document(doc: Docs) = fold(doc, a, b, c, d, e, f)
}