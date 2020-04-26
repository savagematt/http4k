package org.http4k.typesafe.routing.messages

import com.natpryce.Success
import org.http4k.core.HttpMessage

class HeadersReplaceLens<M : HttpMessage>(
    val name: String
) : SimpleLens<M, List<String?>?> {

    override fun get(from: M) =
        Success(if (from.headers.any { it.first.toLowerCase() == name.toLowerCase() })
            from.headerValues(name)
        else
            null)

    @Suppress("UNCHECKED_CAST")
    override fun set(into: M, value: List<String?>?) =
        Success(into.removeHeader(name).headers(value?.map { name to it }
            ?: emptyList()) as M)

    override fun toString() = "Header '$name'"
}