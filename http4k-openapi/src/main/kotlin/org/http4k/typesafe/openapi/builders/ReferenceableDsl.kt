package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiConcept
import org.http4k.typesafe.openapi.Real
import org.http4k.typesafe.openapi.Ref
import org.http4k.typesafe.openapi.Referenceable
import org.http4k.typesafe.openapi.real

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