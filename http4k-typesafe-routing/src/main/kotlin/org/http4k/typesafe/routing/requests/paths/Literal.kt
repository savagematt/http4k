package org.http4k.typesafe.routing.requests.paths

import org.http4k.typesafe.routing.requests.paths.MatchResult.Companion.matchFailure
import org.http4k.typesafe.routing.requests.paths.MatchResult.Companion.matchSuccess

class Literal(expected: String) : Path<Unit> {
    private val expected = expected.replace(leading, "").replace(trailing, "")

    override fun get(from: String): PathResult<Unit> {
        val leadingSlashes = leading.find(from)?.value ?: ""
        val noLeadingSlashes = from.substring(leadingSlashes.length)

        if (!noLeadingSlashes.startsWith(expected))
            return matchFailure("Path did not start with '$expected'", noLeadingSlashes)

        val remaining = noLeadingSlashes.substring(expected.length)

        return if (remaining.isNotBlank() && !remaining.startsWith('/'))
            matchFailure("Path did not match whole contents up to /", noLeadingSlashes)
        else
            matchSuccess(Unit, remaining)

    }

    override fun set(into: String, value: Unit): String =
        into / expected

    operator fun <T> div(next: Path<T>): IgnoreFirst<T> = IgnoreFirst(this, next)
}