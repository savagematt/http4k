package org.http4k.typesafe.routing.requests

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.map
import com.natpryce.recover
import org.http4k.core.Request
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.wrongRoute
import org.http4k.typesafe.routing.messages.SimpleLens
import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.leading

class PathLens<T>(val path: Path<T>) : SimpleLens<Request, T> {
    override fun get(from: Request): Result<T, RoutingError> =
        path.get(from.uri.path)
            .map {
                val remaining = it.remaining.replace(leading, "")
                when {
                    remaining.isNotEmpty() ->
                        wrongRoute("Did not match full path. Remaining: ${remaining}")
                    else ->
                        Success(it.value)
                }
            }
            .recover { wrongRoute("Path did not match. ${it.reason}") }

    override fun set(into: Request, value: T): Result<Request, RoutingError> =
        Success(into.uri(
            into.uri.path(
                path.set(into.uri.path, value))
        ))

    override fun toString(): String = path.toString()
}
