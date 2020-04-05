package org.http4k.typesafe.routing

import com.natpryce.Failure
import org.http4k.core.Body
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.functional.ResultLens


class RoutingErrorException(error: RoutingError) : Exception(error.message)

sealed class RoutingError {
    abstract val message: String
    fun exception() = RoutingErrorException(this)

    /**
     * This is the wrong route to handle the request, and the server should
     * look for another one
     */
    data class WrongRoute(override val message: String) : RoutingError()

    /**
     * This was the correct route to handle the request, so the router should
     * stop looking for other routes.
     *
     * But for some reason the request was not acceptable to the route (for
     * example maybe the body was badly formatted) and the router should return
     * the response provided (although it's free to make it's own modifications
     * first)
     */
    data class RouteFailed(override val message: String, val response: Response) : RoutingError()

    companion object {
        fun wrongRoute(message: String) = Failure(WrongRoute(message))
        fun routeFailed(status: Status, message: String) = routeFailed(message, Response(status))
        fun routeFailed(message: String, response: Response) = Failure(RouteFailed(message, response))
    }
}

interface MessageLens<M : HttpMessage, T> : ResultLens<M, T, RoutingError>

typealias RequestLens<T> = MessageLens<Request, T>

typealias ResponseLens<T> = MessageLens<Response, T>

interface Route<In, Out, TReqLens : RequestLens<In>, TResLens : ResponseLens<Out>>  {
    val request: TReqLens
    val response: TResLens
}