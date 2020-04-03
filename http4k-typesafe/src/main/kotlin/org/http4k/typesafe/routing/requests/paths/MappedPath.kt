package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap

class MappedPath<A, B>(
    val a: Path<A>,
    val getter: (Match<A>) -> PathResult<B>,
    val setter: (B) -> A
) : Path<B> {
    override fun get(from: String): PathResult<B> =
        a.get(from).flatMap {
            getter(it)
        }

    override fun set(into: String, value: B): String =
        a.set(into, setter(value))

}