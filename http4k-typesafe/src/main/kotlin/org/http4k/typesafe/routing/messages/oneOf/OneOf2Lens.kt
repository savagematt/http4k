package org.http4k.typesafe.routing.messages.oneOf

import com.natpryce.Result
import com.natpryce.flatMapFailure
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.data.OneOf2
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.SimpleLens

class OneOf2Lens<M : HttpMessage, A, B>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>
) : SimpleLens<M, OneOf2<A, B>> {
    @Suppress("UNCHECKED_CAST")
    override fun get(from: M) =
        a.get(from)
            .map { OneOf2.A<A, B>(it) }
            .flatMapFailure {
                b.get(from)
                    .map { OneOf2.B<A, B>(it) }
            } as Result<OneOf2<A, B>, RoutingError>

    override fun set(into: M, value: OneOf2<A, B>) =
        when (value) {
            is OneOf2.A -> a.set(into, value.value)
            is OneOf2.B -> b.set(into, value.value)
        }

    infix fun <NEXT> or(next: MessageLens<M, NEXT>) = OneOf3Lens(a, b, next)

    override fun toString() = listOf(a, b).joinToString(" | ")
}