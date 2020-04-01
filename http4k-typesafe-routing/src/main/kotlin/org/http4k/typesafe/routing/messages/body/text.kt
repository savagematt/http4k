package org.http4k.typesafe.routing.messages.body

import com.natpryce.Success
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens

class TextLens<M : HttpMessage> : MessageLens<M, String> {
    override fun get(from: M) =
        Success(from.bodyString())

    @Suppress("UNCHECKED_CAST")
    override fun set(into: M, value: String) =
        Success(
            into.header("Content-Type", TEXT_PLAIN.toHeaderValue())
                .body(value) as M)
}

fun <M : HttpMessage> text() = TextLens<M>()