package org.http4k.typesafe.data

sealed class Tuple

object Tuple0 : Tuple() {
    override fun toString(): String =
        "()"

    fun <T> Tuple1<T>.toList(): List<T> = listOf()
}

data class Tuple1<A>(
    val a: A
) : Tuple() {
    override fun toString(): String =
        "($a)"

    fun <T> Tuple1<T>.toList(): List<T> = listOf(a)
}

data class Tuple2<A, B>(
    val a: A,
    val b: B
) : Tuple() {
    override fun toString(): String =
        "($a, $b)"

    fun <T> Tuple2<T, T>.toList(): List<T> = listOf(a, b)
}

data class Tuple3<A, B, C>(
    val a: A,
    val b: B,
    val c: C
) : Tuple() {
    override fun toString(): String =
        "($a, $b, $c)"

    fun <T> Tuple3<T, T, T>.toList(): List<T> = listOf(a, b, c)
}

data class Tuple4<A, B, C, D>(
    val a: A,
    val b: B,
    val c: C,
    val d: D
) : Tuple() {
    override fun toString(): String =
        "($a, $b, $c, $d)"

    fun <T> Tuple4<T, T, T, T>.toList(): List<T> = listOf(a, b, c, d)
}

data class Tuple5<A, B, C, D, E>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E
) : Tuple() {
    override fun toString(): String =
        "($a, $b, $c, $d, $e)"

    fun <T> Tuple5<T, T, T, T, T>.toList(): List<T> = listOf(a, b, c, d, e)
}

data class Tuple6<A, B, C, D, E, F>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E,
    val f: F
) : Tuple() {
    override fun toString(): String =
        "($a, $b, $c, $d, $e, $f)"

    fun <T> Tuple6<T, T, T, T, T, T>.toList(): List<T> = listOf(a, b, c, d, e, f)
}

data class Tuple7<A, B, C, D, E, F, G>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E,
    val f: F,
    val g: G
) : Tuple() {
    override fun toString(): String =
        "($a, $b, $c, $d, $e, $f, $g)"

    fun <T> Tuple7<T, T, T, T, T, T, T>.toList(): List<T> = listOf(a, b, c, d, e, f, g)
}

data class Tuple8<A, B, C, D, E, F, G, H>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E,
    val f: F,
    val g: G,
    val h: H
) : Tuple() {
    override fun toString(): String =
        "($a, $b, $c, $d, $e, $f, $g, $h)"

    fun <T> Tuple8<T, T, T, T, T, T, T, T>.toList(): List<T> = listOf(a, b, c, d, e, f, g, h)
}

data class Tuple9<A, B, C, D, E, F, G, H, I>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E,
    val f: F,
    val g: G,
    val h: H,
    val i: I
) : Tuple() {
    override fun toString(): String =
        "($a, $b, $c, $d, $e, $f, $g, $h, $i)"

    fun <T> Tuple9<T, T, T, T, T, T, T, T, T>.toList(): List<T> = listOf(a, b, c, d, e, f, g, h, i)
}

data class Tuple10<A, B, C, D, E, F, G, H, I, J>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E,
    val f: F,
    val g: G,
    val h: H,
    val i: I,
    val j: J
) : Tuple() {
    override fun toString(): String =
        "($a, $b, $c, $d, $e, $f, $g, $h, $i, $j)"

    fun <T> Tuple10<T, T, T, T, T, T, T, T, T, T>.toList(): List<T> = listOf(a, b, c, d, e, f, g, h, i, j)
}

fun tuple() = Tuple0

fun <A> tuple(a: A) = Tuple1(a)

fun <A, B> tuple(a: A, b: B) =
    Tuple2(a, b)

fun <A, B, C> tuple(a: A, b: B, c: C) =
    Tuple3(a, b, c)

fun <A, B, C, D> tuple(a: A, b: B, c: C, d: D) =
    Tuple4(a, b, c, d)

fun <A, B, C, D, E> tuple(a: A, b: B, c: C, d: D, e: E) =
    Tuple5(a, b, c, d, e)

fun <A, B, C, D, E, F> tuple(a: A, b: B, c: C, d: D, e: E, f: F) =
    Tuple6(a, b, c, d, e, f)

fun <A, B, C, D, E, F, G> tuple(a: A, b: B, c: C, d: D, e: E, f: F, g: G) =
    Tuple7(a, b, c, d, e, f, g)

fun <A, B, C, D, E, F, G, H> tuple(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H) =
    Tuple8(a, b, c, d, e, f, g, h)

fun <A, B, C, D, E, F, G, H, I> tuple(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I) =
    Tuple9(a, b, c, d, e, f, g, h, i)

fun <A, B, C, D, E, F, G, H, I, J> tuple(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J) =
    Tuple10(a, b, c, d, e, f, g, h, i, j)