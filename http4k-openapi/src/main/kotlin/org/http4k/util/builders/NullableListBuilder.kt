package org.http4k.util.builders

import kotlin.reflect.KFunction1

/**
 * toList() will return null if the initial value was null and
 * no items have been assigned
 */
class NullableListBuilder<T, Dsl : Builder<T, Dsl>>(
    private val initial: List<T>?,
    private val toBuilder: (T) -> Dsl
) {
    constructor(initial: List<T>?, toBuilder: KFunction1<T, Dsl>) : this(initial, { toBuilder.call(it) })

    private var all: MutableList<Dsl>? = initial?.map(toBuilder)?.toMutableList()

    fun ensureValue(): MutableList<Dsl> {
        if (all == null) all = mutableListOf()
        return all!!
    }

    operator fun plusAssign(t: T) {
        ensureValue() += toBuilder(t)
    }

    operator fun plusAssign(t: Collection<T>) {
        ensureValue() += t.map(toBuilder)
    }

    fun map(fn: Dsl.() -> Unit) {
        if (all == null) return
        all!!
            // run fn on each builder
            .map { it.also(fn) }
            .toMutableList()
    }

    fun build(): List<T>? = all?.map { it.build() }?.toList() ?: initial
}