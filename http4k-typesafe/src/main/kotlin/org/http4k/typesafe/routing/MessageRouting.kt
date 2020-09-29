package org.http4k.typesafe.routing

import org.http4k.core.HttpMessage
import org.http4k.format.Json
import org.http4k.typesafe.routing.messages.HeadersAppendLens
import org.http4k.typesafe.routing.messages.HeadersReplaceLens

interface MessageRouting<M : HttpMessage, D> {
    fun any(): MessageLens<M, Unit, D>
    fun nothing(): MessageLens<M, Nothing, D>
    fun text(): MessageLens<M, String, D>
    fun <NODE : Any> json(json: Json<NODE>): MessageLens<M, NODE, D>
    fun header(name: String): MessageLens<M, String?, D>
    fun appendHeader(name: String): MessageLens<M, String?, D>
    fun appendHeaders(name: String): HeadersAppendLens<M>
    fun headers(name: String): HeadersReplaceLens<M>
    fun <T> MessageLens<M, T?, D>.required(onFailure: (() -> RoutingError)? = null): MessageLens<M, T, D>
}