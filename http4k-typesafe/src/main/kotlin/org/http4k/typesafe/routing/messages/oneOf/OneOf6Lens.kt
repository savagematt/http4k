package org.http4k.typesafe.routing.messages.oneOf

import com.natpryce.Result
import com.natpryce.flatMapFailure
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.SimpleLens
import org.http4k.util.data.OneOf6

class OneOf6Lens<M : HttpMessage, A, B, C, D, E, F>(
    val a: MessageLens<M, A, *>,
    val b: MessageLens<M, B, *>,
    val c: MessageLens<M, C, *>,
    val d: MessageLens<M, D, *>,
    val e: MessageLens<M, E, *>,
    val f: MessageLens<M, F, *>
) : SimpleLens<M, OneOf6<A, B, C, D, E, F>> {
    @Suppress("UNCHECKED_CAST")
    override fun get(from: M) =
        a.get(from)
            .map { OneOf6.A<A, B, C, D, E, F>(it) }
            .flatMapFailure {
                b.get(from)
                    .map { OneOf6.B<A, B, C, D, E, F>(it) }
                    .flatMapFailure {
                        c.get(from)
                            .map { OneOf6.C<A, B, C, D, E, F>(it) }
                            .flatMapFailure {
                                d.get(from)
                                    .map { OneOf6.D<A, B, C, D, E, F>(it) }
                                    .flatMapFailure {
                                        e.get(from)
                                            .map { OneOf6.E<A, B, C, D, E, F>(it) }
                                            .flatMapFailure {
                                                f.get(from)
                                                    .map { OneOf6.F<A, B, C, D, E, F>(it) }
                                            }
                                    }
                            }
                    }
            } as Result<OneOf6<A, B, C, D, E, F>, RoutingError>

    override fun set(into: M, value: OneOf6<A, B, C, D, E, F>) =
        when (value) {
            is OneOf6.A -> a.set(into, value.value)
            is OneOf6.B -> b.set(into, value.value)
            is OneOf6.C -> c.set(into, value.value)
            is OneOf6.D -> d.set(into, value.value)
            is OneOf6.E -> e.set(into, value.value)
            is OneOf6.F -> f.set(into, value.value)
        }

    infix fun <NEXT> or(next: MessageLens<M, NEXT, *>) = OneOf7Lens(a, b, c, d, e, f, next)

    override fun toString() = listOf(a, b, c, d, e, f).joinToString(" | ")
}