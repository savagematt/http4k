package org.http4k.typesafe.routing.messages.body

import com.natpryce.Success
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.typesafe.routing.SimpleLens

/**
 * @see [org.http4k.typesafe.routing.Routing.text]
 */
class TextLens<M : HttpMessage> : SimpleLens<M, String> {
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
