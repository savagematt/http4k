package org.http4k.openapi

import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.format.Json
import org.http4k.openapi.ParameterLocation.HEADER
import org.http4k.openapi.ParameterLocation.PATH
import org.http4k.util.Extension
import org.http4k.util.JsonRenderer
import org.http4k.util.Renderable
import org.http4k.util.plus

/**
 * By making this sealed and having all things we might want to render into json inherit from it,
 * we can put rendering in a big when block and let the compiler tell us when we've missed something
 */
sealed class OpenApiConcept {
    /**
     * Open api limits what's extensible, but it's easier all round just making
     * everything extensible.
     */
    abstract val extensions: List<Extension>
}

interface OpenApiRenderer<NODE> : JsonRenderer<OpenApiConcept, NODE>

data class Tag(
    val name: String,
    val description: String? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept()


sealed class Referenceable<T : OpenApiConcept> : OpenApiConcept() {
}

data class Ref<T : OpenApiConcept>(
    val value: String) : Referenceable<T>() {

    override val extensions: List<Extension> = emptyList()

    override fun toString(): String {
        return value
    }
}

data class Real<T : OpenApiConcept>(
    val value: T,
    override val extensions: List<Extension> = emptyList()) : Referenceable<T>()

fun <T : OpenApiConcept> T.real(extensions: List<Extension> = emptyList()) =
    Real(this, extensions)

/**
 * TODO: not yet supported
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#schemaObject
 */
sealed class OpenApiSchema : OpenApiConcept() {
    data class Raw<NODE : Any>(
        val schema: NODE,
        override val extensions: List<Extension> = emptyList()) : OpenApiSchema()

    object Empty : OpenApiSchema(){
        override val extensions: List<Extension> = emptyList()
    }

    companion object {
        val empty = Empty
    }
}

sealed class OpenApiBodyExampleValue : OpenApiConcept() {
    data class Real(
        val value: Any,
        override val extensions: List<Extension> = emptyList()) : OpenApiBodyExampleValue()

    data class External(
        val value: String,
        override val extensions: List<Extension> = emptyList()) : OpenApiBodyExampleValue()
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#exampleObject
 */
data class OpenApiBodyExample(
    val value: OpenApiBodyExampleValue,
    val summary: String? = null,
    val description: String? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept()

/**
 * Each Media Type Object provides schema and examples for the media type identified by its key.
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#mediaTypeObject
 */
data class OpenApiMediaType(
    val examples: Map<String, OpenApiBodyExample> = emptyMap(),
    val schema: Referenceable<OpenApiSchema>? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept()

/**
 * Describes a single request body.
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#requestBodyObject
 */
data class OpenApiRequestBody(
    val description: String,
    val content: Map<ContentType, OpenApiMediaType>? = null,
    val required: Boolean? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept() {
    companion object {
        val empty = OpenApiRequestBody("body")
    }
}

@Suppress("EnumEntryName")
enum class ParameterLocation {
    PATH,
    QUERY,
    HEADER,
    COOKIE
}

/**
 * Describes a single operation parameter.
 *
 * We do not yet support styles
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#parameterObject
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#headerObject
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#pathObject
 */
data class OpenApiParameter(
    val in_: ParameterLocation,
    val name: String,
    val required: Boolean? = if (in_ == PATH) true else null,
    val deprecated: Boolean? = null,
    val description: String? = null,
    val schema: Referenceable<OpenApiSchema>? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept()


/**
 * Describes a single response from an API Operation, including design-time, static links to
 * operations based on the response.
 *
 * TODO: links
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#responseObject
 */
data class OpenApiResponse(
    val description: String,
    val content: Map<ContentType, OpenApiMediaType>? = null,
    val headers: Map<String, OpenApiParameter>? = null,
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept() {

    init {
        headers?.forEach {
            if (it.value.in_ != HEADER)
                throw IllegalArgumentException(
                    "Parameter ${it.key} passed to ${this::class.simpleName} " +
                        "was a ${it.value.in_.name.toLowerCase()} parameter")
        }
    }

    companion object {
        val empty = OpenApiResponse("response")
    }
}

/**
 * A container for the expected responses of an operation. The container maps a HTTP response code
 * to the expected response.
 *
 * The documentation is not necessarily expected to cover all possible HTTP response codes because
 * they may not be known in advance. However, documentation is expected to cover a successful operation
 * response and any known errors.
 *
 * The default MAY be used as a default response object for all HTTP codes that are not covered
 * individually by the specification.
 *
 * The Responses Object MUST contain at least one response code, and it SHOULD be the response for a
 * successful operation call.
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#responsesObject
 */
data class OpenApiResponses(
    val default: Referenceable<OpenApiResponse>? = null,
    val byStatus: Map<Status, Referenceable<OpenApiResponse>>? = null,
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept() {

    operator fun OpenApiResponses.plus(byStatus: Pair<Status, Referenceable<OpenApiResponse>>) =
        this.copy(byStatus = this.byStatus + byStatus)
}

/**
 * Describes a single API operation on a path.
 * TODO: externalDocs, callbacks, servers
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#operationObject
 */
data class OpenApiOperation(
    val responses: OpenApiResponses,
    val tags: List<Tag>? = null,
    val summary: String? = null,
    val description: String? = null,
    val operationId: String? = null,
    val parameters: List<Referenceable<OpenApiParameter>>? = null,
    val requestBody: Referenceable<OpenApiRequestBody>? = null,
    val deprecated: Boolean? = null,
    val security: Map<SecurityId, OpenApiSecurityRequirement>? = null,
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept() {
    companion object {
        val empty = OpenApiOperation(OpenApiResponses())
    }
}


/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#securityRequirementObject
 */
data class OpenApiSecurityRequirement(
    val values: List<String> = emptyList(),
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept() {
    companion object {
        val empty = OpenApiSecurityRequirement()
    }
}

interface IOpenApiSecurityScheme {
    val type: String
    val description: String?
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#securitySchemeObject
 */
sealed class OpenApiSecurityScheme() : OpenApiConcept(), IOpenApiSecurityScheme

@Suppress("EnumEntryName")
enum class ApiKeyLocation {
    QUERY,
    HEADER,
    COOKIE
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#securitySchemeObject
 */
data class OpenApiApiKeySecurity(
    val name: String,
    val in_: ApiKeyLocation,
    override val description: String? = null,
    override val extensions: List<Extension> = emptyList()) : OpenApiSecurityScheme() {
    override val type: String = "apiKey"

    companion object {
        val empty = OpenApiApiKeySecurity("ApiKey", ApiKeyLocation.HEADER)
    }
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#securitySchemeObject
 */
data class OpenApiHttpSecurity(
    val scheme: String,
    val bearerFormat: String? = null,
    override val description: String? = null,
    override val extensions: List<Extension> = emptyList()) : OpenApiSecurityScheme() {

    override val type: String = "http"

    companion object {
        val empty = OpenApiHttpSecurity("bearer")
    }
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#securitySchemeObject
 */
data class OpenApiOpenIdConnectSecurity(
    val openIdConnectUrl: Uri,
    override val description: String? = null,
    override val extensions: List<Extension> = emptyList()) : OpenApiSecurityScheme() {

    override val type: String = "openIdConnect"

    companion object {
        val empty = OpenApiOpenIdConnectSecurity(Uri.of("/"))
    }
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#oauthFlowObject
 */
data class OpenApiOauth2ImplicitFlow(
    val authorizationUrl: Uri,
    val refreshUrl: Uri? = null,
    val scopes: Map<String, String> = emptyMap(),
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept() {
    companion object {
        val empty = OpenApiOauth2ImplicitFlow(Uri.of("/"))
    }
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#oauthFlowObject
 */
data class OpenApiOauth2PasswordFlow(
    val tokenUrl: Uri,
    val refreshUrl: Uri? = null,
    val scopes: Map<String, String> = emptyMap(),
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept() {
    companion object {
        val empty = OpenApiOauth2PasswordFlow(Uri.of("/"))
    }
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#oauthFlowObject
 */
data class OpenApiOauth2ClientCredentialsFlow(
    val tokenUrl: Uri,
    val refreshUrl: Uri? = null,
    val scopes: Map<String, String> = emptyMap(),
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept() {
    companion object {
        val empty = OpenApiOauth2ClientCredentialsFlow(Uri.of("/"))
    }
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#oauthFlowObject
 */
data class OpenApiOauth2AuthorizationCodeFlow(
    val authorizationUrl: Uri,
    val tokenUrl: Uri,
    val refreshUrl: Uri? = null,
    val scopes: Map<String, String> = emptyMap(),
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept() {
    companion object {
        val empty = OpenApiOauth2AuthorizationCodeFlow(Uri.of("/"), Uri.of("/"))
    }
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#oauthFlowsObject
 */
data class OpenApiOauth2Flows(
    val implicit: OpenApiOauth2ImplicitFlow? = null,
    val password: OpenApiOauth2PasswordFlow? = null,
    val clientCredentials: OpenApiOauth2ClientCredentialsFlow? = null,
    val authorizationCode: OpenApiOauth2AuthorizationCodeFlow? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept() {
    companion object {
        val empty = OpenApiOauth2Flows()
    }
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#securitySchemeObject
 */
data class OpenApiOAuth2Security(
    val flows: OpenApiOauth2Flows,
    override val description: String? = null,
    override val extensions: List<Extension> = emptyList()) : OpenApiSecurityScheme() {

    override val type: String = "openIdConnect"

    companion object {
        val empty = OpenApiOAuth2Security(OpenApiOauth2Flows.empty)
    }
}

data class SecurityId(val value: String) {
    override fun toString() = value
}

/**
 * Equivalent to an entry in a Paths object, plus one of its Path Items
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#pathsObject
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#pathItemObject
 *
 * `operation` is not nullable, although the spec says it can be. It makes everything too fiddly to make
 * it nullable- you'll have to edit the json when it's produced. See:
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#security-filtering
 */
data class OpenApiOperationInfo(
    val method: Method,
    val path: String,
    val operation: OpenApiOperation,
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept() {
    companion object {
        val empty = OpenApiOperationInfo(GET, "/", OpenApiOperation.empty)
    }
}

data class SchemaId(val value: String) {
    override fun toString(): String = value
}

/**
 * TODO: support other components:
 * - parameters
 * - examples
 * - requestBodies
 * - headers
 * - securitySchemes
 * - links
 * - callbacks
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#componentsObject
 */
data class OpenApiComponents(
    val securitySchemes: Map<SecurityId, Referenceable<OpenApiSecurityScheme>>? = null,
    val schemas: Map<SchemaId, OpenApiSchema>? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept() {
    companion object {
        val empty = OpenApiComponents()
    }
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#contactObject
 */
data class OpenApiContact(
    val name: String? = null,
    val url: String? = null,
    val email: String? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept() {
    companion object {
        val empty = OpenApiContact()
    }
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#licenseObject
 */
data class OpenApiLicense(
    val name: String,
    val url: String? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept() {
    companion object {
        val empty = OpenApiLicense("name")
    }
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#infoObject
 */
data class OpenApiInfo(
    val title: String,
    val version: String,
    val description: String? = null,
    val termsOfService: String? = null,
    val contact: OpenApiContact? = null,
    val license: OpenApiLicense? = null,
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept() {

    companion object {
        val empty = OpenApiInfo("My api", "0.0")
    }
}

/**
 * This is the root document object of the OpenAPI document.
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#openapi-object
 */
data class OpenApiObject(
    val info: OpenApiInfo,
    val paths: List<OpenApiOperationInfo> = emptyList(),
    val components: OpenApiComponents = OpenApiComponents.empty,
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept() {
    companion object {
        val empty = OpenApiObject(OpenApiInfo.empty)
    }
}

/**
 * Used by lenses to add or make modifications to OpenApiRoute, but also
 * to idempotently add components like the security schemes and object
 * schemas used in this route to the top-level OpenApi.
 *
 * So a single lens can for example both add a security method into
 * components/securitySchemes and add that scheme to a route
 */
data class OpenApiRouteInfo(
    val api: OpenApiObject,
    val route: OpenApiOperationInfo) {
    constructor(route: OpenApiOperationInfo) : this(OpenApiObject.empty, route)
    constructor(api: OpenApiObject) : this(api, OpenApiOperationInfo.empty)

    companion object {
        val empty = OpenApiRouteInfo(OpenApiObject.empty, OpenApiOperationInfo.empty)
    }
}

/**
 * This is here in case we change our mind about the type of OpenApiRouteInfo
 */
fun info(api: OpenApiObject, route: OpenApiOperationInfo) = OpenApiRouteInfo(api, route)
