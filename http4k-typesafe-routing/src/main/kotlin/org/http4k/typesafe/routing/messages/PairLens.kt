package org.http4k.typesafe.routing.messages

import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.SimpleLens


/**
 * @see [org.http4k.typesafe.routing.Routing.and]
 */
class PairLens<M : HttpMessage, A, B>(val a: MessageLens<M, A>, val b: MessageLens<M, B>) : SimpleLens<M, Pair<A, B>> {
    override fun get(from: M) =
        a.get(from).flatMap { a ->
            b.get(from).map { b ->
                a to b
            }
        }

    override fun set(into: M, value: Pair<A, B>) =
        a.set(into, value.first).flatMap { b.set(it, value.second) }
}
