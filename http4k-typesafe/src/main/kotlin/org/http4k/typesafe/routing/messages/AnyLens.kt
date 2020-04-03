package org.http4k.typesafe.routing.messages

import com.natpryce.Success
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.simple.SimpleLens

/**
 * @see [org.http4k.typesafe.routing.MessageRouting.any]
 */
class AnyLens<M : HttpMessage> : SimpleLens<M, Unit> {
    override fun get(from: M) = Success(Unit)

    override fun set(into: M, value: Unit) = Success(into)
}


