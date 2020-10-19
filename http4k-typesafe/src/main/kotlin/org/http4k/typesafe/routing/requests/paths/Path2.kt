package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.routing.fold
import org.http4k.util.data.Tuple2
import org.http4k.util.data.tuple

data class Path2<A, B, Docs>(
    val a: Path<A, Docs>,
    val b: Path<B, Docs>
) : Path<Tuple2<A, B>, Docs> {
    operator fun <T> div(next: Path<T, Docs>) = Path3(a, b, next)

    override fun get(from: String): PathResult<Tuple2<A, B>> =
        a.get(from).flatMap { a ->
            b.get(a.remaining).flatMap { b ->
                matchSuccess(tuple(a.value, b.value), b.remaining)
            }
        }

    override fun set(into: String, value: Tuple2<A, B>): String =
        b.set(b.set(into, value.b), value.b)

    override fun toString() = joinPaths(a, b)

    override fun document(doc: Docs) = fold(doc, a, b)
}