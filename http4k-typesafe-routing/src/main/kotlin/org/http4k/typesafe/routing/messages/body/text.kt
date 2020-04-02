package org.http4k.typesafe.routing.messages.body

import com.natpryce.Success
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.typesafe.routing.MessageLens

class TextLens<M : HttpMessage> : MessageLens<M, String> {
    override fun get(from: M) =
        Success(from.bodyString())

    @Suppress("UNCHECKED_CAST")
    override fun set(into: M, value: String) =
        Success(
            into.textPlain(value) as M)
}

fun <M:HttpMessage> M.textPlain(body: String) =
    this.header("Content-Type", TEXT_PLAIN.toHeaderValue())
        .body(body)

fun Response.textPlain(body: String) =
    this.header("Content-Type", TEXT_PLAIN.toHeaderValue())
        .body(body)

fun Request.textPlain(body: String) =
    this.header("Content-Type", TEXT_PLAIN.toHeaderValue())
        .body(body)

/**
 * Injects some text as the message body, and sets Content-Type to text/plain
 *
 * Extracts message body as a string, without checking Content-Type, because
 * we want to be liberal in what we accept from others
 * https://en.wikipedia.org/wiki/Robustness_principle
 */
fun <M : HttpMessage> text() = TextLens<M>()