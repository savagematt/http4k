package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.routing.fold
import org.http4k.util.data.Tuple3
import org.http4k.util.data.tuple

data class Path3<A, B, C, Docs>(
    val a: Path<A, Docs>,
    val b: Path<B, Docs>,
    val c: Path<C, Docs>
) : Path<Tuple3<A, B, C>, Docs> {
    operator fun <T> div(next: Path<T, Docs>) = Path4(a, b, c, next)

    override fun get(from: String): PathResult<Tuple3<A, B, C>> =
        a.get(from).flatMap { a ->
            b.get(a.remaining).flatMap { b ->
                c.get(b.remaining).flatMap { c ->
                    matchSuccess(tuple(a.value, b.value, c.value), c.remaining)
                }
            }
        }


    override fun set(into: String, value: Tuple3<A, B, C>): String =
        c.set(b.set(a.set(into, value.a), value.b), value.c)

    override fun toString() = joinPaths(a, b, c)

    override fun document(doc: Docs) = fold(doc, a, b, c)
}