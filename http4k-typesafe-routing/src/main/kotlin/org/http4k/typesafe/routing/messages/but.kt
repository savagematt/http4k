package org.http4k.typesafe.routing.messages

import com.natpryce.Result
import com.natpryce.flatMap
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError

class ButLens<M : HttpMessage, B>(
    val unit: MessageLens<M, Unit>,
    val lens: MessageLens<M, B>)
    : MessageLens<M, B> {

    override fun get(from: M): Result<B, RoutingError> =
        unit.get(from).flatMap { lens.get(from) }

    override fun set(into: M, value: B): Result<M, RoutingError> =
        unit.set(into, Unit).flatMap { lens.set(it, value) }
}

/**
 * If I have:
 *
 * ```
 * val method: RequestLens<Unit> = method(GET)
 * val body: RequestLens<String> = text()
 * ```
 *
 * When I combine them using `and`, I get:
 *
 * ```
 * val requestLens: RequestLens<Pair<Unit,String>> = method and body
 * ```
 *
 * But that is a bit clunky, because I don't care about the unit, just
 * the string. So instead I can:
 *
 * ```
 * val requestLens: RequestLens<String> = method but body
 * ```
 */
infix fun <M : HttpMessage, B> MessageLens<M, Unit>.but(lens: MessageLens<M, B>) =
    ButLens(this, lens)
