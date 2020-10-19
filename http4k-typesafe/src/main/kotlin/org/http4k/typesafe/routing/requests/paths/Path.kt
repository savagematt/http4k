package org.http4k.typesafe.routing.requests.paths

import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.Success
import org.http4k.typesafe.routing.Documentable

data class Match<T>(val value: T, val remaining: String)
data class NoMatch(val reason: String)

fun matchFailure(reason: String) = Failure(NoMatch(reason))
fun <T> matchSuccess(value: T, remaining: String) = Success(Match(value, remaining))

typealias PathResult<T> = Result<Match<T>, NoMatch>

interface Path<T, D> : Documentable<D> {
    fun get(from: String): PathResult<T>
    fun set(into: String, value: T): String

    fun invoke(into: String, value: T) = this.set(into, value)
    fun invoke(from: String) = this.get(from)
}

interface SimplePath<T> : Path<T,Any>{
    override fun document(doc: Any) = doc
}