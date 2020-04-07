package org.http4k.typesafe.routing

import com.natpryce.Result
import com.natpryce.Success
import org.http4k.core.Credentials
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.util.functional.Kind
import org.http4k.util.functional.Kind2
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed

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

    /**
     * Returns username as a string
     */
    fun basicAuthServer(validator: (Credentials) -> Result<String, RoutingError>):
        Kind2<TLens, Request, String>

    /**
     * Returns username as a string
     */
    fun basicAuthClient(provider: (String) -> Credentials?):
        Kind2<TLens, Request, String>
}

fun basicAuthValidator(realm:String, validator: (Credentials) -> Boolean): (Credentials) -> Result<String, RoutingError> = {
    if (validator(it))
        Success(it.user)
    else
        routeFailed(
            "Invalid credentials",
            Response(Status.UNAUTHORIZED).header("WWW-Authenticate", "Basic Realm=\"$realm\""))
}