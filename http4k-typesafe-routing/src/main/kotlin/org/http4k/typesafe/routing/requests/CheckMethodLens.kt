package org.http4k.typesafe.routing.requests

import com.natpryce.Result
import com.natpryce.Success
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.SimpleLens

class CheckMethodLens(val method: Method) : SimpleLens<Request, Unit> {
    override fun get(from: Request): Result<Unit, RoutingError> =
        when (from.method) {
            method -> Success(Unit)
            else -> RoutingError.wrongRoute("Method was not $method")
        }

    override fun set(into: Request, value: Unit): Result<Request, RoutingError> =
        Success(into.method(method))

}