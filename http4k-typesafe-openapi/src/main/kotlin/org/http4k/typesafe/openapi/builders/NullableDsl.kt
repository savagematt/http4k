package org.http4k.typesafe.openapi.builders

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