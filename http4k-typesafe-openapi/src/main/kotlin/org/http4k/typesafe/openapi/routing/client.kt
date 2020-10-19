package org.http4k.typesafe.openapi.routing

import org.http4k.core.HttpHandler
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.client
import org.http4k.util.data.Tuple2
import org.http4k.util.data.tuple

fun <In, Out> Route<In, Out, OpenApiRouteDocs>.call(
    http: HttpHandler,
    arg: In): Out =
    (this client http)(arg)

fun <A, B, Out> Route<Tuple2<A, B>, Out, OpenApiRouteDocs>.call(
    http: HttpHandler,
    a: A,
    b: B): Out =
    (this client http)(tuple(a, b))