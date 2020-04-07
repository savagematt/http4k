package org.http4k.typesafe.routing.messages.oneOf

import com.natpryce.Result
import com.natpryce.flatMapFailure
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.util.data.OneOf5
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.SimpleLens

class OneOf5Lens<M : HttpMessage, A, B, C, D, E>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>,
    val d: MessageLens<M, D>,
    val e: MessageLens<M, E>
) : SimpleLens<M, OneOf5<A, B, C, D, E>> {
    @Suppress("UNCHECKED_CAST")
    override fun get(from: M) =
        a.get(from)
            .map { OneOf5.A<A, B, C, D, E>(it) }
            .flatMapFailure {
                b.get(from)
                    .map { OneOf5.B<A, B, C, D, E>(it) }
                    .flatMapFailure {
                        c.get(from)
                            .map { OneOf5.C<A, B, C, D, E>(it) }
                            .flatMapFailure {
                                d.get(from)
                                    .map { OneOf5.D<A, B, C, D, E>(it) }
                                    .flatMapFailure {
                                        e.get(from)
                                            .map { OneOf5.E<A, B, C, D, E>(it) }
                                    }
                            }
                    }
            } as Result<OneOf5<A, B, C, D, E>, RoutingError>

    override fun set(into: M, value: OneOf5<A, B, C, D, E>) =
        when (value) {
            is OneOf5.A -> a.set(into, value.value)
            is OneOf5.B -> b.set(into, value.value)
            is OneOf5.C -> c.set(into, value.value)
            is OneOf5.D -> d.set(into, value.value)
            is OneOf5.E -> e.set(into, value.value)
        }

    infix fun <NEXT> or(next: MessageLens<M, NEXT>) = OneOf6Lens(a, b, c, d, e, next)

    override fun toString() = listOf(a, b, c, d, e).joinToString(" | ")
}