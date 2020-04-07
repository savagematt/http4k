package org.http4k.typesafe.routing

import org.http4k.core.HttpMessage
import org.http4k.format.Json
import org.http4k.util.functional.Kind2
import org.http4k.typesafe.routing.messages.AnyLens
import org.http4k.typesafe.routing.messages.ForSimpleLens
import org.http4k.typesafe.routing.messages.Headers
import org.http4k.typesafe.routing.messages.HeaderReplaceLens
import org.http4k.typesafe.routing.messages.HeadersAppendLens
import org.http4k.typesafe.routing.messages.HeadersReplaceLens
import org.http4k.typesafe.routing.messages.NothingLens
import org.http4k.typesafe.routing.messages.RequiredLens
import org.http4k.typesafe.routing.messages.body.JsonLens
import org.http4k.typesafe.routing.messages.body.TextLens
import org.http4k.typesafe.routing.messages.fix

/*
Grammar
------------------------------------------------------------------
 */
open class SimpleMessageRouting<M : HttpMessage>() : MessageRouting<M, ForSimpleLens> {
    override fun any():
        Kind2<ForSimpleLens, M, Unit> =
        AnyLens()

    override fun nothing() =
        NothingLens<M>()

    override fun text():
        Kind2<ForSimpleLens, M, String> =
        TextLens()

    override fun <NODE : Any> json(json: Json<NODE>):
        Kind2<ForSimpleLens, M, NODE> =
        JsonLens(json)

    override fun header(name: String) =
        HeaderReplaceLens<M>(name)

    override fun appendHeader(name: String) =
        Headers<M>(name)

    override fun appendHeaders(name: String) =
        HeadersAppendLens<M>(name)

    override fun headers(name: String) =
        HeadersReplaceLens<M>(name)

    override fun <T> Kind2<ForSimpleLens, M, T?>.required(onFailure: () -> RoutingError) =
        RequiredLens(this.fix(), onFailure)
}