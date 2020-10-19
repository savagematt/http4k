package org.http4k.typesafe.openapi.routing

import com.natpryce.flatMap
import com.natpryce.map
import com.natpryce.recover
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.typesafe.routing.Route
import org.http4k.util.data.Tuple2
import org.http4k.util.data.tuple

infix fun <In, Out> Route<In, Out, OpenApiRouteDocs>.client(
    http: HttpHandler): (In) -> Out {
    val route = this@client
    return { param: In ->
        route.request.set(Request(GET, "/"), param)
            .map(http)
            .flatMap { response -> route.response.get(response) }
            .recover { throw it.exception() }
    }
}

fun <In, Out> Route<In, Out, OpenApiRouteDocs>.call(
    http: HttpHandler,
    arg: In): Out =
    (this client http)(arg)

fun <A, B, Out> Route<Tuple2<A, B>, Out, OpenApiRouteDocs>.call(
    http: HttpHandler,
    a: A,
    b: B): Out =
    (this client http)(tuple(a, b))