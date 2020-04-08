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

    override fun toString() = "Header '$name'"
}

class HeaderReplaceLens<M : HttpMessage>(
    val name: String
) : SimpleLens<M, String?> {

    override fun get(from: M) =
        Success(from.header(name))

    @Suppress("UNCHECKED_CAST")
    override fun set(into: M, value: String?) =
        when (value) {
            null -> Success(into.removeHeader(name) as M)
            else -> Success(into.replaceHeader(name, value) as M)
        }

    override fun toString() = "Header '$name'"
}

class HeadersAppendLens<M : HttpMessage>(
    val name: String
) : SimpleLens<M, List<String?>> {

    override fun get(from: M) =
        Success(from.headerValues(name))

    @Suppress("UNCHECKED_CAST")
    override fun set(into: M, value: List<String?>) =
        Success(into.headers(value.map { name to it }) as M)

    override fun toString() = "Header '$name'"
}

class HeadersReplaceLens<M : HttpMessage>(
    val name: String
) : SimpleLens<M, List<String?>> {

    override fun get(from: M) =
        Success(from.headerValues(name))

    @Suppress("UNCHECKED_CAST")
    override fun set(into: M, value: List<String?>) =
        Success(into.removeHeader(name).headers(value.map { name to it }) as M)

    override fun toString() = "Header '$name'"
}