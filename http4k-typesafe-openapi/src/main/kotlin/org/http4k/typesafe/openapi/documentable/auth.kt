package org.http4k.typesafe.openapi.documentable

import org.http4k.openapi.OpenApiHttpSecurity
import org.http4k.openapi.OpenApiSecurityRequirement
import org.http4k.openapi.OpenApiSecurityScheme
import org.http4k.openapi.SecurityId
import org.http4k.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.openapi.real

fun basicAuth(securityId: SecurityId = SecurityId("BasicAuth")): OpenApiRouteInfoDsl.() -> Unit =
    securityOf(securityId, OpenApiHttpSecurity("basic"))

/**
 * Returns an extension method that will add security in to `securitySchemes` in
 * [org.http4k.typesafe.openapi.OpenApiObject.components], as well as associating it with
 * [org.http4k.typesafe.openapi.OpenApiOperation.security] for
 * [org.http4k.typesafe.openapi.OpenApiRouteInfo.route]
 */
fun securityOf(id: SecurityId,
               security: OpenApiSecurityScheme,
               requirement: OpenApiSecurityRequirement = OpenApiSecurityRequirement.empty)
    : OpenApiRouteInfoDsl.() -> Unit = {
    api {
        components {
            this.securitySchemes += id to security.real()
        }
    }
    route {
        operation {
            this.security += id to requirement
        }
    }
}