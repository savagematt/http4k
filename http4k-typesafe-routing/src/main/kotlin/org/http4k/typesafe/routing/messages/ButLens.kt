package org.http4k.typesafe.routing.messages

import com.natpryce.Result
import com.natpryce.flatMap
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.SimpleLens

class ButLens<M : HttpMessage, B>(
    val unit: MessageLens<M, Unit>,
    val lens: MessageLens<M, B>)
    : SimpleLens<M, B> {

    override fun get(from: M): Result<B, RoutingError> =
        unit.get(from).flatMap { lens.get(from) }

    override fun set(into: M, value: B): Result<M, RoutingError> =
        unit.set(into, Unit).flatMap { lens.set(it, value) }
}

