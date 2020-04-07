package org.http4k.util.builders

class MapBuilder<K, V, Dsl : Builder<V, Dsl>>(
    initial: Map<K, V>,
    private val toBuilder: (V) -> Dsl) {

    var values: MutableMap<K, Dsl> =
        initial
            .mapValues { (_, v) -> toBuilder(v) }
            .toMutableMap()

    operator fun plusAssign(pair: Pair<K, V>) {
        values.put(pair.first, toBuilder(pair.second))
    }

    operator fun plusAssign(t: Iterable<Pair<K, V>>) {
        values.putAll(t.map { (k, v) -> k to toBuilder(v) })
    }

    fun invoke(f: Map<K, Dsl>.() -> Unit) = f(values)

    fun build(): Map<K, V> = values.mapValues { (_, v) -> v.build() }
}