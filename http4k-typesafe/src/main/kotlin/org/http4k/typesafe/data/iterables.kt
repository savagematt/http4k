package org.http4k.typesafe.data


fun <T, K, I : Iterable<T>> I.checkUnique(
    keyFn: (T) -> K,
    onError: (K) -> Nothing = { throw IllegalStateException("Duplicate key: $it") }): I {
    val keys = mutableSetOf<K>()
    this.forEach {
        val k = keyFn(it)
        if (keys.contains(k))
            onError(k)
        keys.add(k)
    }
    return this
}

