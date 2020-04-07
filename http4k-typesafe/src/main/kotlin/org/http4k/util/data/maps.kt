package org.http4k.util.data

/**
 * NB: appends an entry to a Map<K,V>? (note nullable type)
 */
operator fun <K, V> Map<K, V>?.plus(pair: Pair<K, V>): Map<K, V> =
    (this ?: emptyMap()).let {
        if (it.isEmpty()) mapOf(pair) else LinkedHashMap(it).apply { put(pair.first, pair.second) }
    }
