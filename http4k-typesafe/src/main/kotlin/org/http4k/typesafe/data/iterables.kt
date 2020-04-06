package org.http4k.typesafe.data


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

