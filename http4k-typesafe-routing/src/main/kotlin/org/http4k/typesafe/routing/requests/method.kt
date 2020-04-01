package org.http4k.typesafe.routing.requests

import com.natpryce.Result
import com.natpryce.Success
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.typesafe.routing.RequestLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.wrongRoute


class CheckButDoNotCaptureMethodLens(val method: Method) : RequestLens<Unit> {
    override fun get(from: Request): Result<Unit, RoutingError> =
        when (from.method) {
            method -> Success(Unit)
            else -> wrongRoute("Method was not $method")
        }

    override fun set(into: Request, value: Unit): Result<Request, RoutingError> =
        Success(into.method(method))

}

fun method(method: Method) = CheckButDoNotCaptureMethodLens(method)

class CaptureMethodLens : RequestLens<Method> {
    override fun get(from: Request): Result<Method, RoutingError> =
        Success(from.method)

    override fun set(into: Request, value: Method): Result<Request, RoutingError> =
        Success(into.method(value))
}

fun captureMethod() = CaptureMethodLens()

