package org.http4k.typesafe.openapi

import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.fold
import org.http4k.typesafe.routing.messages.tuples.Tuple10Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple2Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple3Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple4Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple5Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple6Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple7Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple8Lens
import org.http4k.typesafe.routing.messages.tuples.Tuple9Lens
import org.http4k.util.data.Tuple10
import org.http4k.util.data.Tuple2
import org.http4k.util.data.Tuple3
import org.http4k.util.data.Tuple4
import org.http4k.util.data.Tuple5
import org.http4k.util.data.Tuple6
import org.http4k.util.data.Tuple7
import org.http4k.util.data.Tuple8
import org.http4k.util.data.Tuple9

class OpenApiTuple2<M : HttpMessage, A, B>(
    val a: OpenApiLens<M, A>,
    val b: OpenApiLens<M, B>) :
    OpenApiAdapter<M, Tuple2<A, B>>(
        Tuple2Lens<M, A, B>(a, b),
        { fold(it, a, b) }) {

    infix fun <T> with(next: OpenApiLens<M, T>) =
        OpenApiTuple3(a, b, next)

    override fun toString() = "$a & $b"
}

class OpenApiTuple3<M : HttpMessage, A, B, C>(
    val a: OpenApiLens<M, A>,
    val b: OpenApiLens<M, B>,
    val c: OpenApiLens<M, C>
) :
    OpenApiAdapter<M, Tuple3<A, B, C>>(
        Tuple3Lens<M, A, B, C>(a, b, c),
        { fold(it, a, b, c) }) {

    infix fun <T> with(next: OpenApiLens<M, T>) =
        OpenApiTuple4(a, b, c, next)

    override fun toString() = listOf(a, b, c).joinToString(" & ")
}

class OpenApiTuple4<M : HttpMessage, A, B, C, D>(
    val a: OpenApiLens<M, A>,
    val b: OpenApiLens<M, B>,
    val c: OpenApiLens<M, C>,
    val d: OpenApiLens<M, D>
) :
    OpenApiAdapter<M, Tuple4<A, B, C, D>>(
        Tuple4Lens<M, A, B, C, D>(a, b, c, d),
        { fold(it, a, b, c, d) }) {

    infix fun <T> with(next: OpenApiLens<M, T>) =
        OpenApiTuple5(a, b, c, d, next)

    override fun toString() = listOf(a, b, c, d).joinToString(" & ")
}

class OpenApiTuple5<M : HttpMessage, A, B, C, D, E>(
    val a: OpenApiLens<M, A>,
    val b: OpenApiLens<M, B>,
    val c: OpenApiLens<M, C>,
    val d: OpenApiLens<M, D>,
    val e: OpenApiLens<M, E>
) :
    OpenApiAdapter<M, Tuple5<A, B, C, D, E>>(
        Tuple5Lens<M, A, B, C, D, E>(a, b, c, d, e),
        { fold(it, a, b, c, d, e) }) {

    infix fun <T> with(next: OpenApiLens<M, T>) =
        OpenApiTuple6(a, b, c, d, e, next)

    override fun toString() = listOf(a, b, c, d, e).joinToString(" & ")
}

class OpenApiTuple6<M : HttpMessage, A, B, C, D, E, F>(
    val a: OpenApiLens<M, A>,
    val b: OpenApiLens<M, B>,
    val c: OpenApiLens<M, C>,
    val d: OpenApiLens<M, D>,
    val e: OpenApiLens<M, E>,
    val f: OpenApiLens<M, F>
) :
    OpenApiAdapter<M, Tuple6<A, B, C, D, E, F>>(
        Tuple6Lens<M, A, B, C, D, E, F>(a, b, c, d, e, f),
        { fold(it, a, b, c, d, e, f) }) {

    infix fun <T> with(next: OpenApiLens<M, T>) =
        OpenApiTuple7(a, b, c, d, e, f, next)

    override fun toString() = listOf(a, b, c, d, e, f).joinToString(" & ")
}

class OpenApiTuple7<M : HttpMessage, A, B, C, D, E, F, G>(
    val a: OpenApiLens<M, A>,
    val b: OpenApiLens<M, B>,
    val c: OpenApiLens<M, C>,
    val d: OpenApiLens<M, D>,
    val e: OpenApiLens<M, E>,
    val f: OpenApiLens<M, F>,
    val g: OpenApiLens<M, G>
) :
    OpenApiAdapter<M, Tuple7<A, B, C, D, E, F, G>>(
        Tuple7Lens<M, A, B, C, D, E, F, G>(a, b, c, d, e, f, g),
        { fold(it, a, b, c, d, e, f, g) }) {

    infix fun <T> with(next: OpenApiLens<M, T>) =
        OpenApiTuple8(a, b, c, d, e, f, g, next)

    override fun toString() = listOf(a, b, c, d, e, f, g).joinToString(" & ")
}

class OpenApiTuple8<M : HttpMessage, A, B, C, D, E, F, G, H>(
    val a: OpenApiLens<M, A>,
    val b: OpenApiLens<M, B>,
    val c: OpenApiLens<M, C>,
    val d: OpenApiLens<M, D>,
    val e: OpenApiLens<M, E>,
    val f: OpenApiLens<M, F>,
    val g: OpenApiLens<M, G>,
    val h: OpenApiLens<M, H>
) :
    OpenApiAdapter<M, Tuple8<A, B, C, D, E, F, G, H>>(
        Tuple8Lens<M, A, B, C, D, E, F, G, H>(a, b, c, d, e, f, g, h),
        { fold(it, a, b, c, d, e, f, g, h) }) {

    infix fun <T> with(next: OpenApiLens<M, T>) =
        OpenApiTuple9(a, b, c, d, e, f, g, h, next)

    override fun toString() = listOf(a, b, c, d, e, f, g, h).joinToString(" & ")
}

class OpenApiTuple9<M : HttpMessage, A, B, C, D, E, F, G, H, I>(
    val a: OpenApiLens<M, A>,
    val b: OpenApiLens<M, B>,
    val c: OpenApiLens<M, C>,
    val d: OpenApiLens<M, D>,
    val e: OpenApiLens<M, E>,
    val f: OpenApiLens<M, F>,
    val g: OpenApiLens<M, G>,
    val h: OpenApiLens<M, H>,
    val i: OpenApiLens<M, I>
) :
    OpenApiAdapter<M, Tuple9<A, B, C, D, E, F, G, H, I>>(
        Tuple9Lens<M, A, B, C, D, E, F, G, H, I>(a, b, c, d, e, f, g, h, i),
        { fold(it, a, b, c, d, e, f, g, h, i) }) {

    infix fun <T> with(next: OpenApiLens<M, T>) =
        OpenApiTuple10(a, b, c, d, e, f, g, h, i, next)


    override fun toString() = listOf(a, b, c, d, e, f, g, h, i).joinToString(" & ")
}

class OpenApiTuple10<M : HttpMessage, A, B, C, D, E, F, G, H, I, J>(
    val a: OpenApiLens<M, A>,
    val b: OpenApiLens<M, B>,
    val c: OpenApiLens<M, C>,
    val d: OpenApiLens<M, D>,
    val e: OpenApiLens<M, E>,
    val f: OpenApiLens<M, F>,
    val g: OpenApiLens<M, G>,
    val h: OpenApiLens<M, H>,
    val i: OpenApiLens<M, I>,
    val j: OpenApiLens<M, J>
) :
    OpenApiAdapter<M, Tuple10<A, B, C, D, E, F, G, H, I, J>>(
        Tuple10Lens<M, A, B, C, D, E, F, G, H, I, J>(a, b, c, d, e, f, g, h, i, j),
        { fold(it, a, b, c, d, e, f, g, h, i, j) }) {

    override fun toString() = listOf(a, b, c, d, e, f, g, h, i, j).joinToString(" & ")
}
