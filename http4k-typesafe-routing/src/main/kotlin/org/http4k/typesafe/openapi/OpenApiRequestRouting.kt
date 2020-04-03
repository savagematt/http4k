package org.http4k.typesafe.openapi

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.RequestRouting
import org.http4k.typesafe.routing.requests.CheckMethodLens
import org.http4k.typesafe.routing.requests.MethodLens

object OpenApiRequestRouting : OpenApiMessageRouting<Request>(Request::class), RequestRouting<ForOpenApiServerRoute, ForOpenApiRoute, ForOpenApiLens> {
    override fun <T> method(method: Method, rest: Kind2<ForOpenApiLens, Request, T>) =
        CheckMethodLens(method, rest.fix()).asOpenApi {
            rest.fix().document(it).let { info ->
                info.method { method }
            }
        }

    override fun method() =
        MethodLens().asOpenApi()

}