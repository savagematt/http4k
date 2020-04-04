package org.http4k.typesafe.routing

import org.http4k.core.HttpMessage
import org.http4k.typesafe.functional.Kind2

interface MessageRouting<M : HttpMessage, TLens> {
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

    fun replaceHeader(name: String):
        Kind2<TLens, M, String?>

    fun appendHeader(name: String):
        Kind2<TLens, M, String?>

    fun replaceHeaders(name: String):
        Kind2<TLens, M, List<String?>>

    fun appendHeaders(name: String):
        Kind2<TLens, M, List<String?>>
}