package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiOauth2AuthorizationCodeFlow
import org.http4k.util.Appendable
import org.http4k.util.builders.BaseBuilder

class OpenApiOauth2AuthorizationCodeFlowDsl(original: OpenApiOauth2AuthorizationCodeFlow)
    : BaseBuilder<OpenApiOauth2AuthorizationCodeFlow, OpenApiOauth2AuthorizationCodeFlowDsl>(::OpenApiOauth2AuthorizationCodeFlowDsl) {
    var authorizationUrl = original.authorizationUrl
    var tokenUrl = original.tokenUrl
    var refreshUrl = original.refreshUrl
    var scopes = original.scopes.toMutableMap()
    var extensions = Appendable.of(original.extensions)
    override fun invoke(f: OpenApiOauth2AuthorizationCodeFlowDsl.() -> Unit) = f(this)

    override fun build() = OpenApiOauth2AuthorizationCodeFlow(
        authorizationUrl,
        tokenUrl,
        refreshUrl,
        scopes,
        extensions.all

    )

}