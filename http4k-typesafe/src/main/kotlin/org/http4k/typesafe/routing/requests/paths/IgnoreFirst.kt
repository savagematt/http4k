package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap

data class IgnoreFirst<T>(
    val first: Path<Unit>,
    val second: Path<T>) : SimplePath<T> {
    override fun get(from: String): PathResult<T> =
        first.get(from).flatMap {
            second.get(it.remaining)
        }

    override fun set(into: String, value: T): String =
        second.set(first.set(into, Unit), value)

    override fun toString() = joinPaths(first, second)
}