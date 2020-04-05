package org.http4k.typesafe.data

/**
 * NB: appends an entry to a Map<K,V>? (note nullable type)
 */
operator fun <K, V> Map<K, V>?.plus(entry: Pair<K, V>): Map<K, V> =
    (this ?: emptyMap()) + entry
