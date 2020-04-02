package org.http4k.typesafe.routing.requests

import com.natpryce.Result
import com.natpryce.map
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.SimpleLens

class CheckMethodLens<T>(val method: Method,
                         val rest: MessageLens<Request, T>) : SimpleLens<Request, T> {

    override fun get(from: Request): Result<T, RoutingError> =
        when (from.method) {
            method -> rest.get(from)
            else -> RoutingError.wrongRoute("Method was not $method")
        }

    override fun set(into: Request, value: T): Result<Request, RoutingError> =
        rest.set(into, value).map { it.method(method) }
}