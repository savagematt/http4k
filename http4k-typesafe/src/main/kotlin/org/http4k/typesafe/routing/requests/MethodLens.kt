package org.http4k.typesafe.routing.requests

import com.natpryce.Result
import com.natpryce.Success
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.SimpleLens


/**
 * @see [org.http4k.typesafe.routing.RequestRouting.method]
 */
class MethodLens : SimpleLens<Request, Method> {
    override fun get(from: Request): Result<Method, RoutingError> =
        Success(from.method)

    override fun set(into: Request, value: Method): Result<Request, RoutingError> =
        Success(into.method(value))


    override fun toString() = "method"
}

