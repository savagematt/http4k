package org.http4k.typesafe.routing.messages

import com.natpryce.Success
import org.http4k.core.HttpMessage

class HeaderAppendLens<M : HttpMessage>(
    val name: String
) : SimpleLens<M, String?> {

    override fun get(from: M) =
        Success(from.header(name))

    @Suppress("UNCHECKED_CAST")
    override fun set(into: M, value: String?) =
        Success(into.header(name, value) as M)
}