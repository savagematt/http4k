package org.http4k.typesafe.routing

import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.flatMapFailure
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri


class RoutingErrorException(error: RoutingError) : Exception(error.message)

sealed class RoutingError {
    abstract val message: String
    fun exception() = RoutingErrorException(this)

    /**
     * This is the wrong route to handle the request, and the [Router] should
     * look for another one
     */
    data class WrongRoute(override val message: String) : RoutingError()

    /**
     * This was the correct route to handle the request, so the [Router] should
     * stop looking for other routes.
     *
     * But for some reason the request was not acceptable to the route (for
     * example maybe the body was badly formatted) and the [Router] should return
     * [response] (although it's free to make its own modifications first)
     */
    data class RouteFailed(override val message: String, val exception: Exception?, val response: Response) : RoutingError() {
        constructor(message: String, response: Response) : this(message, null, response)
        constructor(message: String, status: Status) : this(message, null, Response(status))
    }

    companion object {
        fun wrongRoute(message: String) = Failure(WrongRoute(message))

        fun routeFailed(message: String, status: Status) = routeFailed(message, Response(status))
        fun routeFailed(message: String, response: Response) = Failure(RouteFailed(message, null, response))
        fun routeFailed(message: String, exception: Exception, response: Response) = Failure(RouteFailed(message, exception, response))
    }
}

interface MessageLens<M : HttpMessage, T, D> : Documentable<D> {
    fun get(from: M): Result<T, RoutingError>
    fun set(into: M, value: T): Result<M, RoutingError>

    operator fun invoke(into: M, value: T) = this.set(into, value)
    operator fun invoke(from: M) = this.get(from)
}

fun <T, D> MessageLens<Request, T, D>.request(value: T): Result<Request, RoutingError> =
    this.set(Request(GET, "/"), value)

fun <T, D> MessageLens<Request, T, D>.uri(value: T): Result<Uri, RoutingError> =
    this.request(value)
        .map { it.uri }
        .flatMapFailure { Failure(it) }

fun <T, D> MessageLens<Response, T, D>.response(value: T): Result<Response, RoutingError> =
    this.set(Response(OK), value)

typealias RequestLens<T, D> = MessageLens<Request, T, D>

typealias ResponseLens<T, D> = MessageLens<Response, T, D>

data class Route<In, Out, D>(
    val request: MessageLens<Request, In, D>,
    val response: MessageLens<Response, Out, D>) : Documentable<D> {
    override fun document(doc: D): D = fold(doc, request, response)

}