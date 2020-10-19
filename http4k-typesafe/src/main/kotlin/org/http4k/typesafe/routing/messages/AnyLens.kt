package org.http4k.typesafe.routing.messages

import com.natpryce.Success
import org.http4k.core.HttpMessage

class AnyLens<M : HttpMessage> : SimpleLens<M, Unit> {
    override fun get(from: M) = Success(Unit)

    override fun set(into: M, value: Unit) = Success(into)

    override fun toString() = "any"
}


