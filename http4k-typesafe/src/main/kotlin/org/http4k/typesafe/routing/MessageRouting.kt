package org.http4k.typesafe.routing

import org.http4k.core.HttpMessage
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.format.Json
import org.http4k.typesafe.functional.Kind2

interface MessageRouting<M : HttpMessage, TLens> {
    /**
     * Injects some text as the message body, and sets Content-Type to text/plain
     *
     * Extracts message body as a string, without checking Content-Type, because
     * we want to be liberal in what we accept from others
     * https://en.wikipedia.org/wiki/Robustness_principle
     */
    fun text(): Kind2<TLens, M, String>

    fun <NODE : Any> json(json: Json<NODE>): Kind2<TLens, M, NODE>

    /**
     * Always successfully gets or sets.
     *
     * gets Unit, and does not modify the message when setting
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

    /**
     * Replaces the existing value(s) of the header
     */
    fun header(name: String):
        Kind2<TLens, M, String?>

    /**
     * Replaces the existing value(s) of the header
     */
    fun headers(name: String):
        Kind2<TLens, M, List<String?>>

    fun appendHeader(name: String):
        Kind2<TLens, M, String?>

    fun appendHeaders(name: String):
        Kind2<TLens, M, List<String?>>

    fun <T> Kind2<TLens, M, T?>.required(
        onFailure: () -> RoutingError =
            { RoutingError.RouteFailed("$this is required", Response(BAD_REQUEST)) }):
        Kind2<TLens, M, T>
}