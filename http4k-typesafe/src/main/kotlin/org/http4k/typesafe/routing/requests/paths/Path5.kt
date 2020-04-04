package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.data.Tuple5
import org.http4k.typesafe.data.tuple

data class Path5<A, B, C, D, E>(
    val a: Path<A>,
    val b: Path<B>,
    val c: Path<C>,
    val d: Path<D>,
    val e: Path<E>
) : SimplePath<Tuple5<A, B, C, D, E>> {
    operator fun <T> div(next: Path<T>) = Path6(a, b, c, d, e, next)

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
}