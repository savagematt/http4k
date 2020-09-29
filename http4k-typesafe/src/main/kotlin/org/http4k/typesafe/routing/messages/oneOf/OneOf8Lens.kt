package org.http4k.typesafe.routing.messages.oneOf

import com.natpryce.Result
import com.natpryce.flatMapFailure
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.SimpleLens
import org.http4k.util.data.OneOf8

class OneOf8Lens<M : HttpMessage, A, B, C, D, E, F, G, H>(
    val a: MessageLens<M, A,*>,
    val b: MessageLens<M, B,*>,
    val c: MessageLens<M, C,*>,
    val d: MessageLens<M, D,*>,
    val e: MessageLens<M, E,*>,
    val f: MessageLens<M, F,*>,
    val g: MessageLens<M, G,*>,
    val h: MessageLens<M, H,*>
) : SimpleLens<M, OneOf8<A, B, C, D, E, F, G, H>> {
    @Suppress("UNCHECKED_CAST")
    override fun get(from: M) =
        a.get(from)
            .map { OneOf8.A<A, B, C, D, E, F, G, H>(it) }
            .flatMapFailure {
                b.get(from)
                    .map { OneOf8.B<A, B, C, D, E, F, G, H>(it) }
                    .flatMapFailure {
                        c.get(from)
                            .map { OneOf8.C<A, B, C, D, E, F, G, H>(it) }
                            .flatMapFailure {
                                d.get(from)
                                    .map { OneOf8.D<A, B, C, D, E, F, G, H>(it) }
                                    .flatMapFailure {
                                        e.get(from)
                                            .map { OneOf8.E<A, B, C, D, E, F, G, H>(it) }
                                            .flatMapFailure {
                                                f.get(from)
                                                    .map { OneOf8.F<A, B, C, D, E, F, G, H>(it) }
                                                    .flatMapFailure {
                                                        g.get(from)
                                                            .map { OneOf8.G<A, B, C, D, E, F, G, H>(it) }
                                                            .flatMapFailure {
                                                                h.get(from)
                                                                    .map { OneOf8.H<A, B, C, D, E, F, G, H>(it) }
                                                            }
                                                    }
                                            }
                                    }
                            }
                    }
            } as Result<OneOf8<A, B, C, D, E, F, G, H>, RoutingError>

    override fun set(into: M, value: OneOf8<A, B, C, D, E, F, G, H>) =
        when (value) {
            is OneOf8.A -> a.set(into, value.value)
            is OneOf8.B -> b.set(into, value.value)
            is OneOf8.C -> c.set(into, value.value)
            is OneOf8.D -> d.set(into, value.value)
            is OneOf8.E -> e.set(into, value.value)
            is OneOf8.F -> f.set(into, value.value)
            is OneOf8.G -> g.set(into, value.value)
            is OneOf8.H -> h.set(into, value.value)
        }

    infix fun <NEXT> or(next: MessageLens<M, NEXT,*>) = OneOf9Lens(a, b, c, d, e, f, g, h, next)

    override fun toString() = listOf(a, b, c, d, e, f, g, h).joinToString(" | ")
}