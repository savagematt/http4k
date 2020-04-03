package org.http4k.typesafe.routing.requests.paths.parsing

import org.http4k.typesafe.routing.requests.paths.Match
import org.http4k.typesafe.routing.requests.paths.matchFailure
import org.http4k.typesafe.routing.requests.paths.matchSuccess

/**
 * Catches IllegalArgumentException from the mapping function and returns
 * failureMessage as a `Failure(NoMatch(reason))`
 */
fun <A,B> mapper(f:(A)->B, failureMessage:String) = { match: Match<A> ->
    try {
        matchSuccess(f(match.value), match.remaining)
    } catch (e: IllegalArgumentException) {
        matchFailure(failureMessage)
    }
}
