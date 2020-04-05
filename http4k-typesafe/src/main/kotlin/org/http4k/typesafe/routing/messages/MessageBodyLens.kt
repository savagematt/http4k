package org.http4k.typesafe.routing.messages

import com.natpryce.map
import org.http4k.core.ContentType
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.BodyLens

class MessageBodyLens<M : HttpMessage, T>(
    val type: ContentType,
    val lens: BodyLens<T>) : SimpleLens<M, T> {
    override fun get(from: M) =
        lens.get(from.body)

    @Suppress("UNCHECKED_CAST")
    override fun set(into: M, value: T) =
        lens.set(into.body, value)
            .map { into.body(it) as M }

    override fun toString() = "${type.value} body"
}
