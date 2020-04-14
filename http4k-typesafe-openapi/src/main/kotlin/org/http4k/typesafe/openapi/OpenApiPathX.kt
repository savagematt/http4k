package org.http4k.typesafe.openapi

import org.http4k.util.data.Tuple10
import org.http4k.util.data.Tuple2
import org.http4k.util.data.Tuple3
import org.http4k.util.data.Tuple4
import org.http4k.util.data.Tuple5
import org.http4k.util.data.Tuple6
import org.http4k.util.data.Tuple7
import org.http4k.util.data.Tuple8
import org.http4k.util.data.Tuple9
import org.http4k.typesafe.routing.requests.paths.Path10
import org.http4k.typesafe.routing.requests.paths.Path2
import org.http4k.typesafe.routing.requests.paths.Path3
import org.http4k.typesafe.routing.requests.paths.Path4
import org.http4k.typesafe.routing.requests.paths.Path5
import org.http4k.typesafe.routing.requests.paths.Path6
import org.http4k.typesafe.routing.requests.paths.Path7
import org.http4k.typesafe.routing.requests.paths.Path8
import org.http4k.typesafe.routing.requests.paths.Path9
import org.http4k.util.fold

data class OpenApiPath2<A, B>(
    val a: OpenApiPath<A>,
    val b: OpenApiPath<B>
) : OpenApiPath<Tuple2<A, B>>(
    Path2(a, b),
    fold(a, b)) {

    infix fun <T> and(next: OpenApiPath<T>) = OpenApiPath3(a, b, next)

}

data class OpenApiPath3<A, B, C>(
    val a: OpenApiPath<A>,
    val b: OpenApiPath<B>,
    val c: OpenApiPath<C>
) : OpenApiPath<Tuple3<A, B, C>>(
    Path3(a, b, c),
    fold(a, b, c)) {

    infix fun <T> and(next: OpenApiPath<T>) = OpenApiPath4(a, b, c, next)
}

data class OpenApiPath4<A, B, C, D>(
    val a: OpenApiPath<A>,
    val b: OpenApiPath<B>,
    val c: OpenApiPath<C>,
    val d: OpenApiPath<D>
) : OpenApiPath<Tuple4<A, B, C, D>>(
    Path4(a, b, c, d),
    fold(a, b, c, d)) {

    infix fun <T> and(next: OpenApiPath<T>) = OpenApiPath5(a, b, c, d, next)
}

data class OpenApiPath5<A, B, C, D, E>(
    val a: OpenApiPath<A>,
    val b: OpenApiPath<B>,
    val c: OpenApiPath<C>,
    val d: OpenApiPath<D>,
    val e: OpenApiPath<E>
) : OpenApiPath<Tuple5<A, B, C, D, E>>(
    Path5(a, b, c, d, e),
    fold(a, b, c, d, e)) {

    infix fun <T> and(next: OpenApiPath<T>) = OpenApiPath6(a, b, c, d, e, next)
}

data class OpenApiPath6<A, B, C, D, E, F>(
    val a: OpenApiPath<A>,
    val b: OpenApiPath<B>,
    val c: OpenApiPath<C>,
    val d: OpenApiPath<D>,
    val e: OpenApiPath<E>,
    val f: OpenApiPath<F>
) : OpenApiPath<Tuple6<A, B, C, D, E, F>>(
    Path6(a, b, c, d, e, f),
    fold(a, b, c, d, e, f)) {

    infix fun <T> and(next: OpenApiPath<T>) = OpenApiPath7(a, b, c, d, e, f, next)
}

data class OpenApiPath7<A, B, C, D, E, F, G>(
    val a: OpenApiPath<A>,
    val b: OpenApiPath<B>,
    val c: OpenApiPath<C>,
    val d: OpenApiPath<D>,
    val e: OpenApiPath<E>,
    val f: OpenApiPath<F>,
    val g: OpenApiPath<G>
) : OpenApiPath<Tuple7<A, B, C, D, E, F, G>>(
    Path7(a, b, c, d, e, f, g),
    fold(a, b, c, d, e, f, g)) {

    infix fun <T> and(next: OpenApiPath<T>) = OpenApiPath8(a, b, c, d, e, f, g, next)
}

data class OpenApiPath8<A, B, C, D, E, F, G, H>(
    val a: OpenApiPath<A>,
    val b: OpenApiPath<B>,
    val c: OpenApiPath<C>,
    val d: OpenApiPath<D>,
    val e: OpenApiPath<E>,
    val f: OpenApiPath<F>,
    val g: OpenApiPath<G>,
    val h: OpenApiPath<H>
) : OpenApiPath<Tuple8<A, B, C, D, E, F, G, H>>(
    Path8(a, b, c, d, e, f, g, h),
    fold(a, b, c, d, e, f, g, h)) {
    infix fun <T> and(next: OpenApiPath<T>) = OpenApiPath9(a, b, c, d, e, f, g, h, next)

}

data class OpenApiPath9<A, B, C, D, E, F, G, H, I>(
    val a: OpenApiPath<A>,
    val b: OpenApiPath<B>,
    val c: OpenApiPath<C>,
    val d: OpenApiPath<D>,
    val e: OpenApiPath<E>,
    val f: OpenApiPath<F>,
    val g: OpenApiPath<G>,
    val h: OpenApiPath<H>,
    val i: OpenApiPath<I>
) : OpenApiPath<Tuple9<A, B, C, D, E, F, G, H, I>>(
    Path9(a, b, c, d, e, f, g, h, i),
    fold(a, b, c, d, e, f, g, h, i)) {
    infix fun <T> and(next: OpenApiPath<T>) = OpenApiPath10(a, b, c, d, e, f, g, h, i, next)

}

data class OpenApiPath10<A, B, C, D, E, F, G, H, I, J>(
    val a: OpenApiPath<A>,
    val b: OpenApiPath<B>,
    val c: OpenApiPath<C>,
    val d: OpenApiPath<D>,
    val e: OpenApiPath<E>,
    val f: OpenApiPath<F>,
    val g: OpenApiPath<G>,
    val h: OpenApiPath<H>,
    val i: OpenApiPath<I>,
    val j: OpenApiPath<J>
) : OpenApiPath<Tuple10<A, B, C, D, E, F, G, H, I, J>>(
    Path10(a, b, c, d, e, f, g, h, i, j),
    fold(a, b, c, d, e, f, g, h, i, j)) {

}