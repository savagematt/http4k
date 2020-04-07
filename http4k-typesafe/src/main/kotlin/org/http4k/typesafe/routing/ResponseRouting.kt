package org.http4k.typesafe.routing

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.util.functional.Kind2

interface ResponseRouting<TLens> : MessageRouting<Response, TLens> {
    /**
     * Matches any `Response` with the correct `status`, and returns the result
     * of `next.get(Response)`
     *
     * Injects status into the result of `rest.set(Response, T)`
     */
    fun <T> Status.with(rest: Kind2<TLens, Response, T>):
        Kind2<TLens, Response, T>

    /**
     * Matches any `Response` and extracts the status.
     *
     * Injects a given status in to `Response`.
     */
    fun status():
        Kind2<TLens, Response, Status>
}