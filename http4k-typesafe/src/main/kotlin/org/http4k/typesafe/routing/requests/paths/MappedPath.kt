package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap


open class MappedPath<A, B, D>(
    val initial: Path<A, D>,
    val getter: (Match<A>) -> PathResult<B>,
    val setter: (B) -> A
) : Path<B, D> {

    override fun get(from: String): PathResult<B> =
        initial.get(from).flatMap {
            getter(it)
        }

    override fun set(into: String, value: B): String =
        initial.set(into, setter(value))

    override fun toString() = initial.toString()

    override fun document(doc: D) = initial.document(doc)
}