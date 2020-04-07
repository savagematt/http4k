package org.http4k.util.merging

/**
 * The use case for [Merge] is that two processes started off with
 * an empty [reference] object, and then independently made some changes.
 * Now we want to merge those changes back into a single object
 *
 * For one field, we might take the most recent value that has changed from
 * the reference version. For another we might merge two maps or two lists,
 * and so on
 */
data class Merge<T>(
    val reference: T,
    val b: T,
    val a: T
) {

    operator fun <V> invoke(f: Merge<T>.() -> V): V = f(this)

    /**
     * Returns the value of [getter] from the first version where
     * it is different to [reference]
     */
    fun <V> latest(getter: (T) -> V): V =
        getter(reference).let { ref ->
            when (val aValue = getter(a)) {
                ref -> getter(b)
                else -> aValue
            }
        }

    fun <V> combine(getter: (T) -> V, f: (a: V, b: V) -> V): V =
        f(getter(a), getter(b))

    /**
     * Drills down using [getter] and gives you another [Merge]
     * object for the value of [getter] in  [reference] vs.
     * the value of [getter] in each of [a] and [b]
     */
    fun <V : Any, R> latest(getter: (T) -> V, f: Merge<V>.() -> R): R =
        getter(this.reference).changes(getter(b), getter(a), f)

    /**
     * Drills down using [getter] and gives you another [Merge]
     * object for the value of [getter] in [reference] vs.
     * the value of [getter] in each of [a] and [b]
     *
     * You need to provide a [nullValue] to be used as reference
     * when [getter] is null in the existing [reference].
     */
    fun <V : Any, R> latest(getter: (T) -> V?, nullValue: V, f: Merge<V>.() -> R): R? {
        val ref = getter(reference) ?: nullValue
        return ref.resolve(
            getter(this.b),
            getter(this.a),
            f)
    }
}

/**
 * Changes made to [a] will take priority over [b]
 */
fun <T : Any, V> T.changes(b: T, a: T, f: Merge<T>.() -> V) =
    this.changes(b, a).invoke(f)

fun <T : Any> T.changes(b: T, a: T) =
    Merge(this, b, a)

fun <T, V> Merge<T>.join(getter: (T) -> List<V>): List<V> =
    combine(getter, { a, b -> a + b })

fun <T, K, V : Any> Merge<T>.merge(
    getter: (T) -> Map<K, V>,
    nullValue: V,
    f: Merge<V>.() -> V
): Map<K, V> {
    val refMap = getter(reference)
    val bMap = getter(b)
    val aMap = getter(a)

    return (aMap.keys + bMap.keys).fold(emptyMap()) { acc, k ->
        val ref: V = refMap[k] ?: nullValue

        val v = ref.resolve(
            bMap[k],
            aMap[k],
            f)

        when (v) {
            null -> acc
            else -> acc + (k to v)
        }
    }
}

fun <T, K, V : Any> Merge<T>.mergeNullable(
    getter: (T) -> Map<K, V>?,
    nullValue: V,
    f: Merge<V>.() -> V
): Map<K, V>? {
    val ref = getter(reference) ?: emptyMap()
    val b = getter(b)
    val a = getter(a)

    return when (b) {
        null -> when (a) {
            null -> null
            else -> ref.changes(ref, a).merge({ it }, nullValue, f)
        }
        else -> when (a) {
            null -> ref.changes(b, ref).merge({ it }, nullValue, f)
            else -> ref.changes(b, a).merge({ it }, nullValue, f)
        }
    }
}

fun <R, V : Any> V.resolve(b: V?, a: V?, f: Merge<V>.() -> R): R? {
    val ref = this

    return when (b) {
        null -> when (a) {
            null -> null
            else -> ref.changes(ref, a, f)
        }
        else -> when (a) {
            null -> ref.changes(b, ref, f)
            else -> ref.changes(b, a, f)
        }
    }
}
