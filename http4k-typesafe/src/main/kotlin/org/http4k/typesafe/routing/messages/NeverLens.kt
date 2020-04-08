package org.http4k.typesafe.routing.messages

import org.http4k.core.HttpMessage

/**
 * @see [org.http4k.typesafe.routing.MessageRouting.nothing]
 */
class NeverLens<M : HttpMessage, T> : SimpleLens<M, T> {
    override fun get(from: M) =
        throw IllegalStateException("This lens should never be called")

    override fun set(into: M, value: T) =
        throw IllegalStateException("This lens should never be called")

    override fun toString() = "never"
}
