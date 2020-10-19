package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.typesafe.routing.fold

data class IgnoreSecond<T, D>(
    val first: Path<T, D>,
    val second: Path<Unit, D>) : Path<T, D> {
    override fun get(from: String): PathResult<T> =
        first.get(from).flatMap { first ->
            second.get(first.remaining).map { second ->
                Match(first.value, second.remaining)
            }
        }

    override fun set(into: String, value: T): String =
        second.set(first.set(into, value), Unit)

    override fun toString() = joinPaths(first, second)

    override fun document(doc: D) = fold(doc, first, second)
}