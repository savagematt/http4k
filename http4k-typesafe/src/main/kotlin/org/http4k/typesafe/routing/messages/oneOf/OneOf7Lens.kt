package org.http4k.typesafe.routing.messages.oneOf

import com.natpryce.Result
import com.natpryce.flatMapFailure
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.util.data.OneOf7
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.SimpleLens

class OneOf7Lens<M : HttpMessage, A, B, C, D, E, F, G>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>,
    val d: MessageLens<M, D>,
    val e: MessageLens<M, E>,
    val f: MessageLens<M, F>,
    val g: MessageLens<M, G>
) : SimpleLens<M, OneOf7<A, B, C, D, E, F, G>> {
    @Suppress("UNCHECKED_CAST")
    override fun get(from: M) =
        a.get(from)
            .map { OneOf7.A<A, B, C, D, E, F, G>(it) }
            .flatMapFailure {
                b.get(from)
                    .map { OneOf7.B<A, B, C, D, E, F, G>(it) }
                    .flatMapFailure {
                        c.get(from)
                            .map { OneOf7.C<A, B, C, D, E, F, G>(it) }
                            .flatMapFailure {
                                d.get(from)
                                    .map { OneOf7.D<A, B, C, D, E, F, G>(it) }
                                    .flatMapFailure {
                                        e.get(from)
                                            .map { OneOf7.E<A, B, C, D, E, F, G>(it) }
                                            .flatMapFailure {
                                                f.get(from)
                                                    .map { OneOf7.F<A, B, C, D, E, F, G>(it) }
                                                    .flatMapFailure {
                                                        g.get(from)
                                                            .map { OneOf7.G<A, B, C, D, E, F, G>(it) }
                                                    }
                                            }
                                    }
                            }
                    }
            } as Result<OneOf7<A, B, C, D, E, F, G>, RoutingError>

    override fun set(into: M, value: OneOf7<A, B, C, D, E, F, G>) =
        when (value) {
            is OneOf7.A -> a.set(into, value.value)
            is OneOf7.B -> b.set(into, value.value)
            is OneOf7.C -> c.set(into, value.value)
            is OneOf7.D -> d.set(into, value.value)
            is OneOf7.E -> e.set(into, value.value)
            is OneOf7.F -> f.set(into, value.value)
            is OneOf7.G -> g.set(into, value.value)
        }

    infix fun <NEXT> or(next: MessageLens<M, NEXT>) = OneOf8Lens(a, b, c, d, e, f, g, next)

    override fun toString() = listOf(a, b, c, d, e, f, g).joinToString(" | ")
}