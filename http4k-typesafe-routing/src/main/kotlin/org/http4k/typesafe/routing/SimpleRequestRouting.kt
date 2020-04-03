package org.http4k.typesafe.routing

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.requests.CheckMethodLens
import org.http4k.typesafe.routing.requests.MethodLens

object SimpleRequestRouting :
    SimpleMessageRouting<Request>(),
    RequestRouting<ForSimpleServerRoute, ForSimpleRoute, ForSimpleLens> {

    override fun <T> method(method: Method, rest: Kind2<ForSimpleLens, Request, T>) =
        CheckMethodLens(method, rest.fix())

    override fun method() =
        MethodLens()

}