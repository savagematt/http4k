package org.http4k.typesafe.routing

import com.natpryce.Result
import org.http4k.core.HttpHandler
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.typesafe.data.OneOf2
import org.http4k.typesafe.data.Tuple2
import org.http4k.typesafe.functional.Kind2

/**
 * For what's going on with these generic parameters, @see [org.http4k.typesafe.functional.Kind2]
 */
interface Routing<TServerRoute, TRoute, TLens, TPath> {
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

    infix fun <M : HttpMessage, A, B> Kind2<TLens, M, A>.or(other: Kind2<TLens, M, B>):
        Kind2<TLens, M, OneOf2<A, B>>

    infix fun <M : HttpMessage, T> Kind2<TLens, M, T>.alternatively(other: Kind2<TLens, M, T>):
        Kind2<TLens, M, T>

    fun <M : HttpMessage, A, B> Kind2<TLens, M, A>.map(
        getter: (A) -> Result<B, RoutingError>,
        setter: (B) -> A):
        Kind2<TLens, M, B>

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

    val path: Paths<TPath>
    val request: RequestRouting<TLens, TPath>
    val response: ResponseRouting<TLens>
}
