package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiConcept
import org.http4k.openapi.Real
import org.http4k.openapi.Ref
import org.http4k.openapi.Referenceable
import org.http4k.openapi.real
import org.http4k.util.builders.BaseBuilder
import org.http4k.util.builders.Builder

class ReferenceableDsl<T : OpenApiConcept, Dsl : Builder<T, Dsl>>(
    original: Referenceable<T>,
    private val asDsl: (T) -> Dsl)
    : BaseBuilder<Referenceable<T>, ReferenceableDsl<T, Dsl>>({ ReferenceableDsl(it, asDsl) }) {

    var value = original

    override fun invoke(f: ReferenceableDsl<T, Dsl>.() -> Unit) = f(this)

    /**
     * Maps over value, if it is a Real value
     */
    fun mapReferenceable(f: Dsl.() -> Unit) =
        value.let {
            when (it) {
                is Real -> value = asDsl(it.value).also(f).build().real()
                is Ref -> TODO("We do not support mapping Refs")
            }
        }

    override fun build() = value

}