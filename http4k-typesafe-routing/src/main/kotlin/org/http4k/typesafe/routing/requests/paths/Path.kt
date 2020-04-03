package org.http4k.typesafe.routing.requests.paths

import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.Success
import org.http4k.typesafe.functional.PolymorphicLens

sealed class MatchResult<T> {
    data class Match<T>(val value: T, val remaining: String) : MatchResult<T>()
    data class NoMatch(val reason: String) : MatchResult<Nothing>()
    companion object {
        fun matchFailure(reason: String, remaining: String) = Failure(NoMatch(reason))
        fun <T> matchSuccess(value: T, remaining: String) = Success(Match(value, remaining))
    }
}
typealias PathResult<T> = Result<MatchResult.Match<T>, MatchResult.NoMatch>

interface Path<T> : PolymorphicLens<String, String, T, PathResult<T>> {
    operator fun div(next: String) = this / Literal(next)
    operator fun div(next: Literal) = IgnoreSecond(this, next)
}

val trailing = Regex("/*$")
val leading = Regex("^/*")

operator fun String.div(path: String) =
    joinPaths(this, path)

operator fun <T> String.div(path: Path<T>) =
    Literal(this) / path

operator fun <A, B> Path<A>.div(next: Path<B>) = Path2(this, next)

fun joinPaths(a: String, b: String) =
    a.replace(trailing, "") + "/" + b.replace(leading, "")
