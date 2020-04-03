package org.http4k.typesafe.openapi

import org.http4k.core.HttpMessage
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.MessageRouting
import org.http4k.typesafe.routing.messages.AnyLens
import org.http4k.typesafe.routing.messages.HeaderReplaceLens
import org.http4k.typesafe.routing.messages.NothingLens
import org.http4k.typesafe.routing.messages.body.TextLens
import kotlin.reflect.KClass

open class OpenApiMessageRouting<M : HttpMessage>(private val clazz: KClass<M>) : MessageRouting<M, ForOpenApiServerRoute, ForOpenApiRoute, ForOpenApiLens> {
    override fun any() =
        AnyLens<M>().asOpenApi()

    override fun nothing() =
        NothingLens<M>().asOpenApi()

    override fun text():
        Kind2<ForOpenApiLens, M, String> =
        TextLens<M>()
            .asOpenApi(documentTextLens(clazz))

    override fun replaceHeader(name: String) =
        HeaderReplaceLens<M>(name)
            .asOpenApi()

}