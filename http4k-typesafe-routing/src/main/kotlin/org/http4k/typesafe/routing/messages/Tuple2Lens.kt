package org.http4k.typesafe.routing.messages

import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.data.Tuple2
import org.http4k.typesafe.data.tuple
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.SimpleLens

class Tuple2Lens<M : HttpMessage, A, B>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>
) : SimpleLens<M, Tuple2<A, B>> {

    infix fun <T> and(next: MessageLens<M, T>) = Tuple3Lens(a, b, next)

    override fun get(from: M) =
        a.get(from).flatMap { a ->
            b.get(from).map { b ->
                tuple(a, b)
            }
        }

    override fun set(into: M, value: Tuple2<A, B>) =
        a.set(into, value.a).flatMap {
            b.set(it, value.b)
        }

}