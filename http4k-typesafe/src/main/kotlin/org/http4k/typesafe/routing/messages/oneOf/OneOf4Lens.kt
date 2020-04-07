package org.http4k.typesafe.routing.messages.oneOf

import com.natpryce.Result
import com.natpryce.flatMapFailure
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.util.data.OneOf4
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.SimpleLens

class OneOf4Lens<M : HttpMessage, A, B, C, D>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>,
    val d: MessageLens<M, D>
) : SimpleLens<M, OneOf4<A, B, C, D>> {
    @Suppress("UNCHECKED_CAST")
    override fun get(from: M) =
        a.get(from)
            .map { OneOf4.A<A, B, C, D>(it) }
            .flatMapFailure {
                b.get(from)
                    .map { OneOf4.B<A, B, C, D>(it) }
                    .flatMapFailure {
                        c.get(from)
                            .map { OneOf4.C<A, B, C, D>(it) }
                            .flatMapFailure {
                                d.get(from)
                                    .map { OneOf4.D<A, B, C, D>(it) }
                            }
                    }
            } as Result<OneOf4<A, B, C, D>, RoutingError>

    override fun set(into: M, value: OneOf4<A, B, C, D>) =
        when (value) {
            is OneOf4.A -> a.set(into, value.value)
            is OneOf4.B -> b.set(into, value.value)
            is OneOf4.C -> c.set(into, value.value)
            is OneOf4.D -> d.set(into, value.value)
        }

    infix fun <NEXT> or(next: MessageLens<M, NEXT>) = OneOf5Lens(a, b, c, d, next)

    override fun toString() = listOf(a, b, c, d).joinToString(" | ")
}