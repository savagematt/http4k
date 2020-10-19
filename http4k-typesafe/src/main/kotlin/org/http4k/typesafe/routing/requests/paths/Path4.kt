package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.routing.fold
import org.http4k.util.data.Tuple4
import org.http4k.util.data.tuple

data class Path4<A, B, C, D, Docs>(
    val a: Path<A, Docs>,
    val b: Path<B, Docs>,
    val c: Path<C, Docs>,
    val d: Path<D, Docs>
) : Path<Tuple4<A, B, C, D>, Docs> {
    operator fun <T> div(next: Path<T, Docs>) = Path5(a, b, c, d, next)

    override fun get(from: String): PathResult<Tuple4<A, B, C, D>> =
        a.get(from).flatMap { a ->
            b.get(a.remaining).flatMap { b ->
                c.get(b.remaining).flatMap { c ->
                    d.get(c.remaining).flatMap { d ->
                        matchSuccess(tuple(a.value, b.value, c.value, d.value), d.remaining)
                    }
                }
            }
        }


    override fun set(into: String, value: Tuple4<A, B, C, D>): String =
        d.set(c.set(b.set(a.set(into, value.a), value.b), value.c), value.d)

    override fun toString() = joinPaths(a, b, c, d)

    override fun document(doc: Docs) = fold(doc, a, b, c, d)
}