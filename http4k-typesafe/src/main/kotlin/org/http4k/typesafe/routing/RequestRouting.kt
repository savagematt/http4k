package org.http4k.typesafe.routing

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.typesafe.functional.Kind
import org.http4k.typesafe.functional.Kind2

interface RequestRouting<TLens, TPath> : MessageRouting<Request, TLens> {
    fun <T> path(path: Kind<TPath, T>): Kind2<TLens, Request, T>

    infix fun <T> Method.bind(path: Kind<TPath, T>): Kind2<TLens, Request, T>

    infix fun Method.bind(path: String): Kind2<TLens, Request, Unit>

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