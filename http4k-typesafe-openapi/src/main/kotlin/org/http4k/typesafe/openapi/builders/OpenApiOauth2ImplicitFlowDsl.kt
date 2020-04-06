package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiOauth2ImplicitFlow
import org.http4k.util.Appendable

class OpenApiOauth2ImplicitFlowDsl(original: OpenApiOauth2ImplicitFlow)
    : BaseBuilder<OpenApiOauth2ImplicitFlow, OpenApiOauth2ImplicitFlowDsl>(::OpenApiOauth2ImplicitFlowDsl) {

    var authorizationUrl = original.authorizationUrl
    var refreshUrl = original.refreshUrl
    var scopes = original.scopes.toMutableMap()
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiOauth2ImplicitFlowDsl.() -> Unit) = f(this)

    override fun build() = OpenApiOauth2ImplicitFlow(
        authorizationUrl,
        refreshUrl,
        scopes,
        extensions.all
    )

}