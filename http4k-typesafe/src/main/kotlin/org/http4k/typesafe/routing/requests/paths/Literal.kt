package org.http4k.typesafe.routing.requests.paths

import org.http4k.typesafe.routing.joinPaths

class Literal(expected: String) : SimplePath<Unit> {
    private val expected = expected.replace(leading, "").replace(trailing, "")

    override fun get(from: String): PathResult<Unit> {
        val leadingSlashes = leading.find(from)?.value ?: ""
        val noLeadingSlashes = from.substring(leadingSlashes.length)

        if (!noLeadingSlashes.startsWith(expected))
            return matchFailure("Path did not start with '$expected'")

        val remaining = noLeadingSlashes.substring(expected.length)

        return if (remaining.isNotBlank() && !remaining.startsWith('/'))
            matchFailure("Path did not match whole contents up to /")
        else
            matchSuccess(Unit, remaining)

    }

    override fun set(into: String, value: Unit): String =
        joinPaths(into, expected)

    operator fun <T> div(next: Path<T>): IgnoreFirst<T> = IgnoreFirst(this, next)

    override fun toString(): String {
        return expected
    }
}