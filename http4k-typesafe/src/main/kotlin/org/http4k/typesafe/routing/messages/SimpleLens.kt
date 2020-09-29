package org.http4k.typesafe.routing.messages

import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens

interface SimpleLens<M : HttpMessage, T> : MessageLens<M, T, Any> {
    override fun document(doc: Any) = doc
}