package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiApiKeySecurity
import org.http4k.typesafe.openapi.OpenApiHttpSecurity
import org.http4k.typesafe.openapi.OpenApiOAuth2Security
import org.http4k.typesafe.openapi.OpenApiOpenIdConnectSecurity
import org.http4k.typesafe.openapi.OpenApiSecurityScheme
import org.http4k.util.Appendable

class OpenApiSecuritySchemeDsl(original: OpenApiSecurityScheme)
    : BaseBuilder<OpenApiSecurityScheme, OpenApiSecuritySchemeDsl>(::OpenApiSecuritySchemeDsl) {
    override fun invoke(f: OpenApiSecuritySchemeDsl.() -> Unit) = f(this)

    private var apiKey: ApiKey? = null
    private var http: Http? = null
    private var oauth2: OAuth2? = null
    private var openIdConnect: OpenIdConnect? = null

    init {
        when (original) {
            is OpenApiApiKeySecurity -> {
                this.apiKey = ApiKey(original)
            }
            is OpenApiHttpSecurity -> {
                this.http = Http(original)
            }
            is OpenApiOAuth2Security -> {
                this.oauth2 = OAuth2(original)
            }
            is OpenApiOpenIdConnectSecurity -> {
                this.openIdConnect = OpenIdConnect(original)
            }
        }
    }

    private fun nullEverything() {
        apiKey = null
        http = null
        oauth2 = null
        openIdConnect = null
    }

    fun apiKey(f: ApiKey.() -> Unit, emptyValue: OpenApiApiKeySecurity = OpenApiApiKeySecurity.empty) {
        if (apiKey == null) {
            nullEverything()
            apiKey = apiKey ?: ApiKey(emptyValue)
        }
        f(apiKey!!)
    }

    fun http(f: Http.() -> Unit, emptyValue: OpenApiHttpSecurity = OpenApiHttpSecurity.empty) {
        if (http == null) {
            nullEverything()
            http = http ?: Http(emptyValue)
        }
        f(http!!)
    }

    fun oauth2(f: OAuth2.() -> Unit, emptyValue: OpenApiOAuth2Security = OpenApiOAuth2Security.empty) {
        if (oauth2 == null) {
            nullEverything()
            oauth2 = oauth2 ?: OAuth2(emptyValue)
        }
        f(oauth2!!)
    }

    fun openIdConnect(
        f: OpenIdConnect.() -> Unit,
        emptyValue: OpenApiOpenIdConnectSecurity = OpenApiOpenIdConnectSecurity.empty) {
        if (openIdConnect == null) {
            nullEverything()
            openIdConnect = openIdConnect ?: OpenIdConnect(emptyValue)
        }
        f(openIdConnect!!)
    }

    override fun build() =
        apiKey?.build()
            ?: http?.build()
            ?: oauth2?.build()
            ?: openIdConnect?.build()
            ?: throw IllegalStateException("Programmer error in ${this::class.simpleName}" +
                "exactly one of either real or external should have a value")

    class ApiKey(original: OpenApiApiKeySecurity)
        : BaseBuilder<OpenApiApiKeySecurity, ApiKey>(::ApiKey) {
        var name = original.name
        var in_ = original.in_
        var description = original.description
        var extensions = Appendable.of(original.extensions)

        override fun invoke(f: ApiKey.() -> Unit) = f(this)

        override fun build() = OpenApiApiKeySecurity(
            name,
            in_,
            description,
            extensions.all
        )

    }

    class Http(original: OpenApiHttpSecurity)
        : BaseBuilder<OpenApiHttpSecurity, Http>(::Http) {
        var scheme = original.scheme
        var bearerFormat = original.bearerFormat
        var description = original.description
        var extensions = Appendable.of(original.extensions)

        override fun invoke(f: Http.() -> Unit) = f(this)

        override fun build() = OpenApiHttpSecurity(
            scheme,
            bearerFormat,
            description,
            extensions.all
        )

    }

    class OAuth2(original: OpenApiOAuth2Security)
        : BaseBuilder<OpenApiOAuth2Security, OAuth2>(::OAuth2) {
        var flows = OpenApiOauth2FlowsDsl(original.flows)
        var description = original.description
        var extensions = Appendable.of(original.extensions)

        override fun invoke(f: OAuth2.() -> Unit) = f(this)

        override fun build() = OpenApiOAuth2Security(
            flows.build(),
            description,
            extensions.all
        )

    }

    class OpenIdConnect(original: OpenApiOpenIdConnectSecurity)
        : BaseBuilder<OpenApiOpenIdConnectSecurity, OpenIdConnect>(::OpenIdConnect) {
        var openIdConnectUrl = original.openIdConnectUrl
        var description = original.description
        var extensions = Appendable.of(original.extensions)

        override fun invoke(f: OpenIdConnect.() -> Unit) = f(this)

        override fun build() = OpenApiOpenIdConnectSecurity(
            openIdConnectUrl,
            description,
            extensions.all
        )

    }

}

