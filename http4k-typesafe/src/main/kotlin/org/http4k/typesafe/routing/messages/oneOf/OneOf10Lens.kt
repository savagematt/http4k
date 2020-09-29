package org.http4k.typesafe.routing.messages.oneOf

import com.natpryce.Result
import com.natpryce.flatMapFailure
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.SimpleLens
import org.http4k.util.data.OneOf10

class OneOf10Lens<M : HttpMessage, A, B, C, D, E, F, G, H, I, J>(
    val a: MessageLens<M, A, *>,
    val b: MessageLens<M, B, *>,
    val c: MessageLens<M, C, *>,
    val d: MessageLens<M, D, *>,
    val e: MessageLens<M, E, *>,
    val f: MessageLens<M, F, *>,
    val g: MessageLens<M, G, *>,
    val h: MessageLens<M, H, *>,
    val i: MessageLens<M, I, *>,
    val j: MessageLens<M, J, *>
) : SimpleLens<M, OneOf10<A, B, C, D, E, F, G, H, I, J>> {
    @Suppress("UNCHECKED_CAST")
    override fun get(from: M) =
        a.get(from)
            .map { OneOf10.A<A, B, C, D, E, F, G, H, I, J>(it) }
            .flatMapFailure {
                b.get(from)
                    .map { OneOf10.B<A, B, C, D, E, F, G, H, I, J>(it) }
                    .flatMapFailure {
                        c.get(from)
                            .map { OneOf10.C<A, B, C, D, E, F, G, H, I, J>(it) }
                            .flatMapFailure {
                                d.get(from)
                                    .map { OneOf10.D<A, B, C, D, E, F, G, H, I, J>(it) }
                                    .flatMapFailure {
                                        e.get(from)
                                            .map { OneOf10.E<A, B, C, D, E, F, G, H, I, J>(it) }
                                            .flatMapFailure {
                                                f.get(from)
                                                    .map { OneOf10.F<A, B, C, D, E, F, G, H, I, J>(it) }
                                                    .flatMapFailure {
                                                        g.get(from)
                                                            .map { OneOf10.G<A, B, C, D, E, F, G, H, I, J>(it) }
                                                            .flatMapFailure {
                                                                h.get(from)
                                                                    .map { OneOf10.H<A, B, C, D, E, F, G, H, I, J>(it) }
                                                                    .flatMapFailure {
                                                                        i.get(from)
                                                                            .map { OneOf10.I<A, B, C, D, E, F, G, H, I, J>(it) }
                                                                            .flatMapFailure {
                                                                                j.get(from)
                                                                                    .map { OneOf10.J<A, B, C, D, E, F, G, H, I, J>(it) }
                                                                            }
                                                                    }
                                                            }
                                                    }
                                            }
                                    }
                            }
                    }
            } as Result<OneOf10<A, B, C, D, E, F, G, H, I, J>, RoutingError>

    override fun set(into: M, value: OneOf10<A, B, C, D, E, F, G, H, I, J>) =
        when (value) {
            is OneOf10.A -> a.set(into, value.value)
            is OneOf10.B -> b.set(into, value.value)
            is OneOf10.C -> c.set(into, value.value)
            is OneOf10.D -> d.set(into, value.value)
            is OneOf10.E -> e.set(into, value.value)
            is OneOf10.F -> f.set(into, value.value)
            is OneOf10.G -> g.set(into, value.value)
            is OneOf10.H -> h.set(into, value.value)
            is OneOf10.I -> i.set(into, value.value)
            is OneOf10.J -> j.set(into, value.value)
        }

    override fun toString() = listOf(a, b, c, d, e, f, g, h, i, j).joinToString(" | ")
}