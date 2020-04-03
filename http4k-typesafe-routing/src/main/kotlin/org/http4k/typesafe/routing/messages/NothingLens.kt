package org.http4k.typesafe.routing.messages

import com.natpryce.Success
import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.SimpleLens
import java.lang.IllegalStateException

/**
 * @see [org.http4k.typesafe.routing.Routing.nothing]
 */
class NothingLens<M : HttpMessage> : SimpleLens<M, Nothing> {
    override fun get(from: M) =
        throw IllegalStateException("Expected nothing")

    override fun set(into: M, value: Nothing) =
        throw IllegalStateException("Expected nothing")
}
