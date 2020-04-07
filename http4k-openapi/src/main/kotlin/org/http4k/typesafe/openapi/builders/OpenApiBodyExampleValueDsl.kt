package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiBodyExampleValue
import org.http4k.util.Appendable

class OpenApiBodyExampleValueDsl(original: OpenApiBodyExampleValue)
    : BaseBuilder<OpenApiBodyExampleValue, OpenApiBodyExampleValueDsl>(::OpenApiBodyExampleValueDsl) {
    override fun invoke(f: OpenApiBodyExampleValueDsl.() -> Unit) = f(this)

    private var real: Real? = null
    private var external: External? = null

    init {
        when (original) {
            is OpenApiBodyExampleValue.Real -> {
                this.real = Real(original)
            }
            is OpenApiBodyExampleValue.External -> {
                this.external = External(original)
            }
        }
    }

    fun real(f: Real.() -> Unit, emptyValue: Any) {
        if (real == null) {
            external = null
            real = real ?: Real(OpenApiBodyExampleValue.Real(emptyValue))
        }
        f(real!!)
    }

    fun external(f: External.() -> Unit, emptyValue: String) {
        if (external == null) {
            real = null
            external = external ?: External(OpenApiBodyExampleValue.External(emptyValue))
        }
        f(external!!)
    }

    override fun build() =
        real?.build()
            ?: external?.build()
            ?: throw IllegalStateException("Programmer error in ${this::class.simpleName}" +
                "exactly one of either real or external should have a value")

    class Real(original: OpenApiBodyExampleValue.Real)
        : BaseBuilder<OpenApiBodyExampleValue.Real, Real>(::Real) {
        var value = original.value
        var extensions = Appendable.of(original.extensions)

        override fun invoke(f: Real.() -> Unit) = f(this)

        override fun build() = OpenApiBodyExampleValue.Real(
            value,
            extensions.all
        )

    }

    class External(original: OpenApiBodyExampleValue.External)
        : BaseBuilder<OpenApiBodyExampleValue.External, External>(::External) {
        var value = original.value
        var extensions = Appendable.of(original.extensions)

        override fun invoke(f: External.() -> Unit) = f(this)

        override fun build() = OpenApiBodyExampleValue.External(
            value,
            extensions.all
        )

    }

}

