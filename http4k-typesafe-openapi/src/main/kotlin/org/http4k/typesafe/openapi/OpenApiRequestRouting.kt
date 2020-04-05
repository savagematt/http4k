package org.http4k.typesafe.openapi

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.typesafe.functional.Kind
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.openapi.OpenApiPaths.literal
import org.http4k.typesafe.routing.RequestRouting
import org.http4k.typesafe.routing.requests.CheckMethodLens
import org.http4k.typesafe.routing.requests.MethodLens
import org.http4k.typesafe.routing.requests.PathLens

object OpenApiRequestRouting :
    OpenApiMessageRouting<Request>(Request::class),
    RequestRouting<ForOpenApiLens, ForOpenApiPath> {

    override fun <T> path(path: Kind<ForOpenApiPath, T>): OpenApiLens<Request, T> {
        val p = path.fix()
        return PathLens(p)
            .asOpenApi {
                p.document(it)
            }
    }

    override fun <T> Method.bind(
        path: Kind<ForOpenApiPath, T>) =
        method(
            this,
            PathLens(path.fix())
                .asOpenApi(fold(path.fix())))

    override fun Method.bind(
        path: String): Kind2<ForOpenApiLens, Request, Unit> {
        return method(this, path(literal(path)))
    }

    override fun <T> method(method: Method, rest: Kind2<ForOpenApiLens, Request, T>) =
        CheckMethodLens(method, rest.fix())
            .asOpenApi {
                rest.fix().document(it).let { info ->
                    info.method { method }
                }
            }

    override fun method() =
        MethodLens().asOpenApi()
}
