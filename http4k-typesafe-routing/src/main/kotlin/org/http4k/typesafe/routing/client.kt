package org.http4k.typesafe.routing

import com.natpryce.flatMap
import com.natpryce.map
import com.natpryce.recover
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request

/**
 * An http client for a given Route, which is a function
 * that take In and returns Out
 *
 * Will throw a RoutingErrorException if anything goes wrong,
 * rather than returning a result. The Route itself should
 * cover all expected server behaviour, including known error
 * cases, so by definition a routing failure here is
 * unexpected, and is a genuine exception.
 */
infix fun <In, Out> Route<In, Out>.client(http: HttpHandler): (In) -> Out =
    { value: In ->
        request.set(Request(GET, "/"), value)
            .map(http)
            .flatMap(response::get)
            .recover { throw it.exception() }
    }