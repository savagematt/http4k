package org.http4k.typesafe.openapi.eg

import com.natpryce.Success
import org.http4k.core.HttpMessage
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.routing.map
import org.http4k.typesafe.routing.messages.LensValueMapper
import kotlin.reflect.KFunction1

interface HasValue<T> {
    val value: T
}

interface HasId<T> {
    val id: T
}

@Suppress("unused")
open class Value<T>(
    override val value: T
) : HasValue<T> {

    override fun toString(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value<*>

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}

fun <M : HttpMessage, B> OpenApiLens<M, String>.string(ctor: (String) -> B): OpenApiLens<M, B> =
    this.map({ Success(ctor(it)) }, { it.toString() })

fun <M : HttpMessage, A, B : Value<A>> OpenApiLens<M, A>.value(ctor: KFunction1<A, B>): OpenApiLens<M, B> =
    this.map({ Success(ctor(it)) }, { it.value })

fun <M : HttpMessage, A, B : Value<A>> OpenApiLens<M, A>.map(constructor: KFunction1<A, B>): OpenApiLens<M, B> {
    return this.map(mapper(constructor))
}

fun <T, V : Value<T>> mapper(ctor: KFunction1<T, V>): LensValueMapper<T, V> {
    return org.http4k.typesafe.routing.messages.mapper(
        { Success(ctor(it)) },
        { it.value }
    )
}