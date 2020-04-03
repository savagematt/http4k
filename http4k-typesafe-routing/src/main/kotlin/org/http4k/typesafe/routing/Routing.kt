package org.http4k.typesafe.routing

import com.natpryce.Result
import org.http4k.core.HttpHandler
import org.http4k.core.HttpMessage
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.data.Tuple2
import org.http4k.typesafe.functional.Kind2

interface MessageRouting<M : HttpMessage, TServerRoute, TRoute, TLens> {
    /**
     * Injects some text as the message body, and sets Content-Type to text/plain
     *
     * Extracts message body as a string, without checking Content-Type, because
     * we want to be liberal in what we accept from others
     * https://en.wikipedia.org/wiki/Robustness_principle
     */
    fun text():
        Kind2<TLens, M, String>

    /**
     * Always successfully gets or sets.
     *
     * Returns Unit, and does not modify the message when setting
     */
    fun any():
        Kind2<TLens, M, Unit>

    /**
     * Throws exceptions if it is ever used to get or set.
     *
     * It should logically exist, but it's not yet clear what it
     * can be used for
     */
    fun nothing():
        Kind2<TLens, M, Nothing>


    fun replaceHeader(name: String):
        Kind2<TLens, M, String?>
}

interface RequestRouting<TServerRoute, TRoute, TLens> : MessageRouting<Request, TServerRoute, TRoute, TLens> {

    /**
     * Matches any `Request` with the correct `method`, and returns the result
     * of `next.get(Request)`
     *
     * Injects `method` into the result of `rest.set(Request, T)`
     */
    fun <T> method(method: Method, rest: Kind2<TLens, Request, T>):
        Kind2<TLens, Request, T>

    /**
     * Matches any `Request` and extracts the `method`.
     *
     * Injects a given `method` in to `Request`.
     */
    fun method():
        Kind2<TLens, Request, Method>
}

interface ResponseRouting<TServerRoute, TRoute, TLens> : MessageRouting<Response, TServerRoute, TRoute, TLens> {
    /**
     * Matches any `Response` with the correct `status`, and returns the result
     * of `next.get(Response)`
     *
     * Injects status into the result of `rest.set(Response, T)`
     */
    fun <T> status(status: Status, rest: Kind2<TLens, Response, T>):
        Kind2<TLens, Response, T>

    /**
     * Matches any `Response` and extracts the status.
     *
     * Injects a given status in to `Response`.
     */
    fun status():
        Kind2<TLens, Response, Status>
}

/**
 * For what's going on with these generic parameters, @see [org.http4k.typesafe.functional.Kind2]
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


    infix fun <M : HttpMessage, A, B> Kind2<TLens, M, A>.and(other: Kind2<TLens, M, B>):
        Kind2<TLens, M, Tuple2<A, B>>

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

    val response: ResponseRouting<TServerRoute, TRoute, TLens>
    val request: RequestRouting<TServerRoute, TRoute, TLens>
}
