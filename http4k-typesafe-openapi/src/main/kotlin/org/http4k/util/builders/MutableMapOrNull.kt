package org.http4k.util.builders

class MutableMapOrNull<K, V>(private val initial: Map<K, V>?) {
    var all: MutableMap<K, V>? = initial?.toMutableMap()

    fun all(): MutableMap<K, V> {
        if (all == null) all = mutableMapOf()
        return all!!
    }

    operator fun plusAssign(t: Pair<K, V>) {
        all() += t
    }

    operator fun plusAssign(t: Iterable<Pair<K, V>>) {
        all() += t
    }

    fun map(fn: (Map.Entry<K, V>) -> V): MutableMapOrNull<K, V> {
        all = all().mapValues(fn).toMutableMap()
        return this
    }

    fun toMap() = all?.toMap() ?: initial
}