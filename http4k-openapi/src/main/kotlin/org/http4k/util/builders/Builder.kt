package org.http4k.util.builders

import kotlin.reflect.KFunction1

/**
 * `TThis` refers to this pattern:
 * https://en.wikipedia.org/wiki/Curiously_recurring_template_pattern
 */
interface Builder<T, TThis : Builder<T, TThis>> {
    operator fun invoke(f: TThis.() -> Unit)

    operator fun T.invoke(f: TThis.() -> Unit): T

    fun build(): T
}

abstract class BaseBuilder<T, Dsl : Builder<T, Dsl>>(
    protected val toBuilder: (T) -> Dsl
) : Builder<T, Dsl> {
    constructor(ctor: KFunction1<T, Dsl>) : this({ ctor.call(it) })

//    // standard invoke() implementation should look like this:
//    override operator fun invoke(f: Dsl.() -> Unit) = f(this)

    override operator fun T.invoke(f: Dsl.() -> Unit): T = toBuilder(this).also(f).build()
}