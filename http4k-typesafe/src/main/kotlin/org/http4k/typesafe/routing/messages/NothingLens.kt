package org.http4k.typesafe.routing.messages

import org.http4k.core.HttpMessage

/**
 * @see [org.http4k.typesafe.routing.MessageRouting.nothing]
 */
@Suppress("KDocUnresolvedReference")
class NothingLens<M : HttpMessage> : SimpleLens<M, Nothing> {
    override fun get(from: M) =
        throw IllegalStateException("Expected nothing")

    override fun set(into: M, value: Nothing) =
        throw IllegalStateException("Expected nothing")

    override fun toString() = "nothing"
}
