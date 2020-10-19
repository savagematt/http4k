package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.typesafe.routing.fold

data class IgnoreFirst<T, D>(
    val first: Path<Unit, D>,
    val second: Path<T, D>) : Path<T, D> {
    override fun get(from: String): PathResult<T> =
        first.get(from).flatMap {
            second.get(it.remaining)
        }

    override fun set(into: String, value: T): String =
        second.set(first.set(into, Unit), value)

    override fun toString() = joinPaths(first, second)

    override fun document(doc: D): D = fold(doc, first, second)
}