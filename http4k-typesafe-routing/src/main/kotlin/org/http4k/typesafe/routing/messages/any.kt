package org.http4k.typesafe.routing.messages

import com.natpryce.Success
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens

class AnyLens<M : HttpMessage> : MessageLens<M, Unit> {
    override fun get(from: M) = Success(Unit)

    override fun set(into: M, value: Unit) = Success(into)
}

/**
 * Always successfully gets or sets.
 *
 * Returns Unit, and does not modify the message when setting
 */
fun <M : HttpMessage> any() = AnyLens<M>()

