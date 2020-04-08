package org.http4k.typesafe.routing

import org.http4k.core.HttpMessage
import org.http4k.format.Json
import org.http4k.typesafe.routing.messages.AnyLens
import org.http4k.typesafe.routing.messages.HeaderAppendLens
import org.http4k.typesafe.routing.messages.HeaderReplaceLens
import org.http4k.typesafe.routing.messages.HeadersAppendLens
import org.http4k.typesafe.routing.messages.HeadersReplaceLens
import org.http4k.typesafe.routing.messages.NothingLens
import org.http4k.typesafe.routing.messages.RequiredLens
import org.http4k.typesafe.routing.messages.body.JsonLens
import org.http4k.typesafe.routing.messages.body.TextLens

open class MessageRouting<M : HttpMessage>() {
    fun any():
        MessageLens<M, Unit> =
        AnyLens()

    fun nothing() =
        NothingLens<M>()

    fun text():
        MessageLens<M, String> =
        TextLens()

    fun <NODE : Any> json(json: Json<NODE>):
        MessageLens<M, NODE> =
        JsonLens(json)

    fun header(name: String) =
        HeaderReplaceLens<M>(name)

    fun appendHeader(name: String) =
        HeaderAppendLens<M>(name)

    fun appendHeaders(name: String) =
        HeadersAppendLens<M>(name)

    fun headers(name: String) =
        HeadersReplaceLens<M>(name)

    fun <T> MessageLens<M, T?>.required(onFailure: (() -> RoutingError)? = null) =
        RequiredLens(this, onFailure)
}