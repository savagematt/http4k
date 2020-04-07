package org.http4k.openapi.builders

import org.http4k.util.builders.BaseBuilder
import org.http4k.util.builders.Builder

class NullableDsl<T, Dsl : Builder<T, Dsl>>(
    original: T?,
    private val empty: () -> T,
    private val asDsl: (T) -> Dsl)
    : BaseBuilder<T?, NullableDsl<T, Dsl>>({
    NullableDsl(it, empty, asDsl)
}) {

    var value = original?.let(asDsl)

    fun invoke(value: T?) {
        this.value = value?.let(asDsl)
    }

    override fun invoke(f: NullableDsl<T, Dsl>.() -> Unit) = f(this)

    /**
     * Maps over value, creating an empty value first if necessary
     */
    fun mapNullable(f: Dsl.() -> Unit) {
        if (value == null) value = asDsl(empty())
        map(f)
    }

    fun map(f: Dsl.() -> Unit) = value?.let(f)

    override fun build(): T? = value?.build()
}