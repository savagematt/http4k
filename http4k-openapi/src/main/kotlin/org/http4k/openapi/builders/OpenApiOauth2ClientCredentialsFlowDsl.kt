package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiOauth2ClientCredentialsFlow
import org.http4k.util.Appendable
import org.http4k.util.builders.BaseBuilder

class OpenApiOauth2ClientCredentialsFlowDsl(original: OpenApiOauth2ClientCredentialsFlow)
    : BaseBuilder<OpenApiOauth2ClientCredentialsFlow, OpenApiOauth2ClientCredentialsFlowDsl>(::OpenApiOauth2ClientCredentialsFlowDsl){

    var tokenUrl = original.tokenUrl
    var refreshUrl = original.refreshUrl
    var scopes = original.scopes.toMutableMap()
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiOauth2ClientCredentialsFlowDsl.() -> Unit) =f(this)

    override fun build()= OpenApiOauth2ClientCredentialsFlow(
        tokenUrl,
        refreshUrl,
        scopes,
        extensions.all
    )
}