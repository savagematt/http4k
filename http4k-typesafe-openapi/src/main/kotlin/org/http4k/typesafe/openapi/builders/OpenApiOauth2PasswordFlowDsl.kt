package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiOauth2PasswordFlow
import org.http4k.util.Appendable

class OpenApiOauth2PasswordFlowDsl(original: OpenApiOauth2PasswordFlow)
    : BaseBuilder<OpenApiOauth2PasswordFlow, OpenApiOauth2PasswordFlowDsl>(::OpenApiOauth2PasswordFlowDsl) {
    var tokenUrl = original.tokenUrl
    var refreshUrl = original.refreshUrl
    var scopes = original.scopes.toMutableMap()
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiOauth2PasswordFlowDsl.() -> Unit) = f(this)

    override fun build() = OpenApiOauth2PasswordFlow(
        tokenUrl,
        refreshUrl,
        scopes,
        extensions.all
    )

}