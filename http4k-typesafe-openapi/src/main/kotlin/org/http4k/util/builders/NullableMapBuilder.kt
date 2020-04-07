package org.http4k.util.builders

import org.http4k.typesafe.openapi.builders.Builder

class NullableMapBuilder<K, V, Dsl : Builder<V, Dsl>>(
    initial: Map<K, V>?,
    private val toBuilder: (V) -> Dsl) {

    private var all: MutableMap<K, Dsl>? =
        initial
            ?.mapValues { (_, v) -> toBuilder(v) }
            ?.toMutableMap()

    fun ensureValue(): MutableMap<K, Dsl> {
        if (all == null) all = mutableMapOf()
        return all!!
    }

    operator fun plusAssign(pair: Pair<K, V>) {
        ensureValue() += pair.first to toBuilder(pair.second)
    }

    operator fun plusAssign(t: Iterable<Pair<K, V>>) {
        ensureValue().putAll(t.map { (k, v) -> k to toBuilder(v) })
    }

    fun map(f: Dsl.() -> Unit) = all?.mapValues { f(it.value) }

    fun invoke(f: Map<K, Dsl>.() -> Unit) = f(ensureValue())

    fun build(): Map<K, V>? = all?.mapValues { (_, v) -> v.build() }
}