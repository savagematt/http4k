package org.http4k.typesafe.routing

import org.http4k.core.HttpMessage
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.messages.AnyLens
import org.http4k.typesafe.routing.messages.ForSimpleLens
import org.http4k.typesafe.routing.messages.HeaderReplaceLens
import org.http4k.typesafe.routing.messages.NothingLens
import org.http4k.typesafe.routing.messages.body.TextLens

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

    override fun replaceHeader(name: String) =
        HeaderReplaceLens<M>(name)
}