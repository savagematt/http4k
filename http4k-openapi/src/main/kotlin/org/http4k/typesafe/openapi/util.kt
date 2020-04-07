package org.http4k.typesafe.openapi

/**
 * NB: appends an entry to a Map<K,V>? (note nullable type)
 */
operator fun <K, V> Map<K, V>?.plus(pair: Pair<K, V>): Map<K, V> =
    (this ?: emptyMap()).let {
        if (it.isEmpty()) mapOf(pair) else LinkedHashMap(it).apply { put(pair.first, pair.second) }
    }

fun <T, K, I : Iterable<T>> I.checkUnique(
    keyFn: (T) -> K,
    onError: (Pair<K,T>) -> Nothing = { throw IllegalStateException("Duplicate key: ${it.first}") }): I {
    val keys = mutableSetOf<K>()
    this.forEach {
        val k = keyFn(it)
        if (keys.contains(k))
            onError(k to it)
        keys.add(k)
    }
    return this
}
