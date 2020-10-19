package org.http4k.typesafe.openapi.routing

import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.typesafe.openapi.OpenApiServerRoute
import org.http4k.typesafe.routing.Route
import org.http4k.util.data.Tuple2
import org.http4k.util.data.Tuple3

infix fun <In, Out> Route<In, Out, OpenApiRouteDocs>.server(
    handler: (In) -> Out) =
    OpenApiServerRoute(this, handler)

infix fun <A, B, Out> Route<Tuple2<A, B>, Out, OpenApiRouteDocs>.server(
    handler: (A, B) -> Out) =
    OpenApiServerRoute(this) { handler(it.a, it.b) }

infix fun <A, B, C, Out> Route<Tuple3<A, B, C>, Out, OpenApiRouteDocs>.server(
    handler: (A, B, C) -> Out) =
    OpenApiServerRoute(this) { handler(it.a, it.b, it.c) }