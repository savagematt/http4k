package org.http4k.util.builders

/**
 * toList() will return null if the initial value was null and
 * no items have been assigned
 */
class AppendableOrNull<T>(private val initial: List<T>?) {
    var all: MutableList<T>? = initial?.toMutableList()

    fun all(): MutableList<T> {
        if (all == null) all = mutableListOf()
        return all!!
    }

    operator fun plusAssign(t: T) {
        all() += t
    }

    operator fun plusAssign(t: Collection<T>) {
        all() += t
    }

    fun map(fn: (T) -> T): AppendableOrNull<T> {
        all = all().map(fn).toMutableList()
        return this
    }

    fun toList() = all?.toList() ?: initial
}