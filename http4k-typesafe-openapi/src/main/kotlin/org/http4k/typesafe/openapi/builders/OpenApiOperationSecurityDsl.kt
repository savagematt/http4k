package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiOperationSecurity
import org.http4k.util.Appendable

class OpenApiOperationSecurityDsl(original: OpenApiOperationSecurity)
    : BaseBuilder<OpenApiOperationSecurity, OpenApiOperationSecurityDsl>(::OpenApiOperationSecurityDsl) {

    var values = Appendable(original.values.toMutableList())
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiOperationSecurityDsl.() -> Unit) = f(this)

    override fun build() = OpenApiOperationSecurity(
        values.all,
        extensions.all
    )

}