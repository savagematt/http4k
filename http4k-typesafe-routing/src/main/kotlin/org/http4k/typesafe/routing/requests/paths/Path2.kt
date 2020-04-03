package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.data.Tuple2
import org.http4k.typesafe.data.tuple

data class Path2<A, B>(
    val a: Path<A>,
    val b: Path<B>
) : Path<Tuple2<A, B>> {
    operator fun <T> div(next: Path<T>) = Path3(a, b, next)

    override fun get(from: String): PathResult<Tuple2<A, B>> =
        a.get(from).flatMap { a ->
            b.get(a.remaining).flatMap { b ->
                matchSuccess(tuple(a.value, b.value), b.remaining)
            }
        }


    override fun set(into: String, value: Tuple2<A, B>): String =
        b.set(b.set(into, value.b), value.b)

}