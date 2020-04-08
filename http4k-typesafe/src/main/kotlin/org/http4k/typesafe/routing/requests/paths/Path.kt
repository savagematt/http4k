package org.http4k.typesafe.routing.requests.paths

import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.Success
import org.http4k.util.functional.PolymorphicLens

data class Match<T>(val value: T, val remaining: String)
data class NoMatch(val reason: String)

fun matchFailure(reason: String) = Failure(NoMatch(reason))
fun <T> matchSuccess(value: T, remaining: String) = Success(Match(value, remaining))

typealias PathResult<T> = Result<Match<T>, NoMatch>

interface Path<T> : PolymorphicLens<String, String, T, PathResult<T>>


fun <A, B> Path<A>.map(getter: (Match<A>) -> PathResult<B>,
                       setter: (B) -> A) =
    MappedPath(this, getter, setter)

fun <A, B> Path<A>.map(getterSetter: Pair<(A) -> B, (B) -> A>) =
    this.map(
        { match ->
            matchSuccess(
                getterSetter.first(match.value),
                match.remaining)
        },
        getterSetter.second)

