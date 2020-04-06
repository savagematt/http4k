package org.http4k.typesafe.routing.messages.oneOf

import com.natpryce.Result
import com.natpryce.flatMapFailure
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.data.OneOf3
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.SimpleLens

class OneOf3Lens<M : HttpMessage, A, B, C>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>
) : SimpleLens<M, OneOf3<A, B, C>> {
    @Suppress("UNCHECKED_CAST")
    override fun get(from: M) =
        a.get(from)
            .map { OneOf3.A<A, B, C>(it) }
            .flatMapFailure {
                b.get(from)
                    .map { OneOf3.B<A, B, C>(it) }
                    .flatMapFailure {
                        c.get(from)
                            .map { OneOf3.C<A, B, C>(it) }
                    }
            } as Result<OneOf3<A, B, C>, RoutingError>

    override fun set(into: M, value: OneOf3<A, B, C>) =
        when (value) {
            is OneOf3.A -> a.set(into, value.value)
            is OneOf3.B -> b.set(into, value.value)
            is OneOf3.C -> c.set(into, value.value)
        }

    infix fun <NEXT> or(next: MessageLens<M, NEXT>) = OneOf4Lens(a, b, c, next)

    override fun toString() = listOf(a, b, c).joinToString(" | ")
}