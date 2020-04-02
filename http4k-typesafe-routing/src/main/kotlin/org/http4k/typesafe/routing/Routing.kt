package org.http4k.typesafe.routing

import com.natpryce.Result
import org.http4k.core.HttpHandler
import org.http4k.core.HttpMessage
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.functional.Kind2
import kotlin.reflect.KClass

sealed class MessageType<M : HttpMessage>(val clazz: KClass<M>) {
    object request : MessageType<Request>(Request::class)
    object response : MessageType<Response>(Response::class)
}

/**
 * For what's going on with these generic parameters, @see [Kind2]
 */
interface Routing<TServerRoute, TRoute, TLens> {
    /**
     * Creates an http client for a given Route, which is a function
     * that takes In and returns Out
     *
     * The function will use the request lens to serialise the In parameter
     * into an empty Request, pass the request to the server HttpHandler,
     * then use route.response lens to deserialise the Response.
     *
     * Will throw a RoutingErrorException if anything goes wrong,
     * rather than returning a result. The Route itself should
     * cover all expected server behaviour, including known error
     * cases, so by definition a routing failure here is
     * unexpected, and is a genuine exception.
     */
    infix fun <In, Out> Kind2<TRoute, In, Out>.consume(http: HttpHandler):
        (In) -> Out

    infix fun <In, Out> Kind2<TRoute, In, Out>.serve(handler: (In) -> Out):
        Kind2<TServerRoute, In, Out>

    fun <In, Out> route(request: Kind2<TLens, Request, In>,
                        response: Kind2<TLens, Response, Out>):
        Kind2<TRoute, In, Out>

    /**
     * Injects some text as the message body, and sets Content-Type to text/plain
     *
     * Extracts message body as a string, without checking Content-Type, because
     * we want to be liberal in what we accept from others
     * https://en.wikipedia.org/wiki/Robustness_principle
     */
    fun <M : HttpMessage> text(type: MessageType<M>):
        Kind2<TLens, M, String>

    /**
     * Always successfully gets or sets.
     *
     * Returns Unit, and does not modify the message when setting
     */
    fun <M : HttpMessage> any(type: MessageType<M>):
        Kind2<TLens, M, Unit>


    infix fun <M : HttpMessage, A, B> Kind2<TLens, M, A>.and(other: Kind2<TLens, M, B>):
        Kind2<TLens, M, Pair<A, B>>

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
    infix fun <M : HttpMessage, T> Kind2<TLens, M, Unit>.but(other: Kind2<TLens, M, T>):
        Kind2<TLens, M, T>

    fun <M : HttpMessage, T, E> result(success: Kind2<TLens, M, T>, failure: Kind2<TLens, M, E>):
        Kind2<TLens, M, Result<T, E>>

    fun <T> status(status: Status, rest: Kind2<TLens, Response, T>):
        Kind2<TLens, Response, T>

    fun status():
        Kind2<TLens, Response, Status>

    fun <T> method(method: Method, rest: Kind2<TLens, Request, T>):
        Kind2<TLens, Request, T>

    fun method():
        Kind2<TLens, Request, Method>
}
