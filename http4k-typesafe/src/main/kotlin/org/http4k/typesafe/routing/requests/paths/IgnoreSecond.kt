package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import com.natpryce.map

data class IgnoreSecond<T>(
    val first: Path<T>,
    val second: Path<Unit>) : SimplePath<T> {
    override fun get(from: String): PathResult<T> =
        first.get(from).flatMap { first ->
            second.get(first.remaining).map { second ->
                Match(first.value, second.remaining)
            }
        }

    override fun set(into: String, value: T): String =
        second.set(first.set(into, value), Unit)

}