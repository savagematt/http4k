package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiOauth2AuthorizationCodeFlow
import org.http4k.typesafe.openapi.OpenApiOauth2ClientCredentialsFlow
import org.http4k.typesafe.openapi.OpenApiOauth2Flows
import org.http4k.typesafe.openapi.OpenApiOauth2ImplicitFlow
import org.http4k.typesafe.openapi.OpenApiOauth2PasswordFlow
import org.http4k.util.Appendable

class OpenApiOauth2FlowsDsl(original: OpenApiOauth2Flows)
    : BaseBuilder<OpenApiOauth2Flows, OpenApiOauth2FlowsDsl>(::OpenApiOauth2FlowsDsl) {

    var implicit = NullableDsl(
        original.implicit,
        { OpenApiOauth2ImplicitFlow.empty },
        ::OpenApiOauth2ImplicitFlowDsl)
    var password = NullableDsl(
        original.password,
        { OpenApiOauth2PasswordFlow.empty },
        ::OpenApiOauth2PasswordFlowDsl)
    var clientCredentials = NullableDsl(
        original.clientCredentials,
        { OpenApiOauth2ClientCredentialsFlow.empty },
        ::OpenApiOauth2ClientCredentialsFlowDsl)
    var authorizationCode = NullableDsl(
        original.authorizationCode,
        { OpenApiOauth2AuthorizationCodeFlow.empty },
        ::OpenApiOauth2AuthorizationCodeFlowDsl)
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: OpenApiOauth2FlowsDsl.() -> Unit) = f(this)

    override fun build() = OpenApiOauth2Flows(
        implicit.build(),
        password.build(),
        clientCredentials.build(),
        authorizationCode.build(),
        extensions.all
    )

}