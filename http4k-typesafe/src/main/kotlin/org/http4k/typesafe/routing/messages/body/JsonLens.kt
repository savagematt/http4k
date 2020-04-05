package org.http4k.typesafe.routing.messages.body

import com.natpryce.Success
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.HttpMessage
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.format.Json
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.http4k.typesafe.routing.messages.SimpleLens

class JsonLens<M : HttpMessage, NODE>(val json: Json<NODE>) : SimpleLens<M, NODE> {
    override fun get(from: M) = try {
        Success(json.parse(from.bodyString()))
    } catch (e: Exception) {
        routeFailed(BAD_REQUEST, "Invalid json. ${e.message}")
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(into: M, value: NODE) =
        Success(into
            .replaceHeader("Content-Type", APPLICATION_JSON.toHeaderValue())
            .body(json.compact(value)) as M)

    override fun toString() = "application/json body"
}
