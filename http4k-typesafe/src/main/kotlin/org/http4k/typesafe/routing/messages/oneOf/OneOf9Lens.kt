package org.http4k.typesafe.routing.messages.oneOf

import com.natpryce.Result
import com.natpryce.flatMapFailure
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.data.OneOf9
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.SimpleLens

class OneOf9Lens<M : HttpMessage, A, B, C, D, E, F, G, H, I>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>,
    val d: MessageLens<M, D>,
    val e: MessageLens<M, E>,
    val f: MessageLens<M, F>,
    val g: MessageLens<M, G>,
    val h: MessageLens<M, H>,
    val i: MessageLens<M, I>
) : SimpleLens<M, OneOf9<A, B, C, D, E, F, G, H, I>> {
    @Suppress("UNCHECKED_CAST")
    override fun get(from: M) =
        a.get(from)
            .map { OneOf9.A<A, B, C, D, E, F, G, H, I>(it) }
            .flatMapFailure {
                b.get(from)
                    .map { OneOf9.B<A, B, C, D, E, F, G, H, I>(it) }
                    .flatMapFailure {
                        c.get(from)
                            .map { OneOf9.C<A, B, C, D, E, F, G, H, I>(it) }
                            .flatMapFailure {
                                d.get(from)
                                    .map { OneOf9.D<A, B, C, D, E, F, G, H, I>(it) }
                                    .flatMapFailure {
                                        e.get(from)
                                            .map { OneOf9.E<A, B, C, D, E, F, G, H, I>(it) }
                                            .flatMapFailure {
                                                f.get(from)
                                                    .map { OneOf9.F<A, B, C, D, E, F, G, H, I>(it) }
                                                    .flatMapFailure {
                                                        g.get(from)
                                                            .map { OneOf9.G<A, B, C, D, E, F, G, H, I>(it) }
                                                            .flatMapFailure {
                                                                h.get(from)
                                                                    .map { OneOf9.H<A, B, C, D, E, F, G, H, I>(it) }
                                                                    .flatMapFailure {
                                                                        i.get(from)
                                                                            .map { OneOf9.I<A, B, C, D, E, F, G, H, I>(it) }
                                                                    }
                                                            }
                                                    }
                                            }
                                    }
                            }
                    }
            } as Result<OneOf9<A, B, C, D, E, F, G, H, I>, RoutingError>

    override fun set(into: M, value: OneOf9<A, B, C, D, E, F, G, H, I>) =
        when (value) {
            is OneOf9.A -> a.set(into, value.value)
            is OneOf9.B -> b.set(into, value.value)
            is OneOf9.C -> c.set(into, value.value)
            is OneOf9.D -> d.set(into, value.value)
            is OneOf9.E -> e.set(into, value.value)
            is OneOf9.F -> f.set(into, value.value)
            is OneOf9.G -> g.set(into, value.value)
            is OneOf9.H -> h.set(into, value.value)
            is OneOf9.I -> i.set(into, value.value)
        }

    infix fun <NEXT> or(next: MessageLens<M, NEXT>) = OneOf10Lens(a, b, c, d, e, f, g, h, i, next)

    override fun toString() = listOf(a, b, c, d, e, f, g, h, i).joinToString(" | ")
}