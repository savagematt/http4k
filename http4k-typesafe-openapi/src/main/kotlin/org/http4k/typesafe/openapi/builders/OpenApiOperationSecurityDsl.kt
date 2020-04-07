package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiSecurityRequirement
import org.http4k.util.Appendable

class OpenApiOperationSecurityDsl(original: OpenApiSecurityRequirement)
    : BaseBuilder<OpenApiSecurityRequirement, OpenApiOperationSecurityDsl>(::OpenApiOperationSecurityDsl) {

    var values = Appendable(original.values.toMutableList())
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiOperationSecurityDsl.() -> Unit) = f(this)

    override fun build() = OpenApiSecurityRequirement(
        values.all,
        extensions.all
    )

}