package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.routing.fold
import org.http4k.util.data.Tuple5
import org.http4k.util.data.tuple

data class Path5<A, B, C, D, E, Docs>(
    val a: Path<A, Docs>,
    val b: Path<B, Docs>,
    val c: Path<C, Docs>,
    val d: Path<D, Docs>,
    val e: Path<E, Docs>
) : Path<Tuple5<A, B, C, D, E>, Docs> {
    operator fun <T> div(next: Path<T, Docs>) = Path6(a, b, c, d, e, next)

    override fun get(from: String): PathResult<Tuple5<A, B, C, D, E>> =
        a.get(from).flatMap { a ->
            b.get(a.remaining).flatMap { b ->
                c.get(b.remaining).flatMap { c ->
                    d.get(c.remaining).flatMap { d ->
                        e.get(d.remaining).flatMap { e ->
                            matchSuccess(tuple(a.value, b.value, c.value, d.value, e.value), e.remaining)
                        }
                    }
                }
            }
        }


    override fun set(into: String, value: Tuple5<A, B, C, D, E>): String =
        e.set(d.set(c.set(b.set(a.set(into, value.a), value.b), value.c), value.d), value.e)

    override fun toString() = joinPaths(a, b, c, d, e)

    override fun document(doc: Docs) = fold(doc, a, b, c, d, e)
}