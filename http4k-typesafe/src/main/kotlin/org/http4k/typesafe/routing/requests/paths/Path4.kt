package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.util.data.Tuple4
import org.http4k.util.data.tuple
import org.http4k.typesafe.routing.joinPaths

data class Path4<A, B, C, D>(
    val a: Path<A>,
    val b: Path<B>,
    val c: Path<C>,
    val d: Path<D>
) : SimplePath<Tuple4<A, B, C, D>> {
    operator fun <T> div(next: Path<T>) = Path5(a, b, c, d, next)

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

    override fun toString() = joinPaths(a,b,c,d)
}