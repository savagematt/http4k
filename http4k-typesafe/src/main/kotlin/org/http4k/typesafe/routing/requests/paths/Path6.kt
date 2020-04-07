package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.util.data.Tuple6
import org.http4k.util.data.tuple
import org.http4k.typesafe.routing.joinPaths

data class Path6<A, B, C, D, E, F>(
    val a: Path<A>,
    val b: Path<B>,
    val c: Path<C>,
    val d: Path<D>,
    val e: Path<E>,
    val f: Path<F>
) : SimplePath<Tuple6<A, B, C, D, E, F>> {
    operator fun <T> div(next: Path<T>) = Path7(a, b, c, d, e, f, next)

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

    override fun toString() = joinPaths(a,b,c,d,e,f)
}