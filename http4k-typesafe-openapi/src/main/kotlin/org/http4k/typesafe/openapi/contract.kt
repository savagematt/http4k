package org.http4k.typesafe.openapi

import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Status

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
data class OpenApiSchema(
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept() {
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
    path,
    query,
    header,
    cookie
}

interface IOpenApiParameter {
    val name: String
    @Suppress("PropertyName")
    val in_: ParameterLocation
    val required: Boolean?
    val deprecated: Boolean?
    val description: String?
    val schema: Referenceable<OpenApiSchema>?
    val extensions: List<Extension>

}

/**
 * Describes a single operation parameter.
 *
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#parameterObject
 *
 * We do not yet support styles
 */
sealed class OpenApiParameter() : OpenApiConcept(), IOpenApiParameter

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#headerObject
 */
data class OpenApiHeader(
    override val name: String,
    override val required: Boolean? = null,
    override val deprecated: Boolean? = null,
    override val description: String? = null,
    override val schema: Referenceable<OpenApiSchema>? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiParameter() {
    override val in_ = ParameterLocation.header
}

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#pathObject
 */
@Suppress("unused")
data class OpenApiPathParam(
    override val name: String,
    override val deprecated: Boolean? = null,
    override val description: String? = null,
    override val schema: Referenceable<OpenApiSchema>? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiParameter() {
    override val in_ = ParameterLocation.path
    /**
     * Path parameters are always required. See: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#parameterObject
     */
    override val required: Boolean = true
}

@Suppress("unused")
data class OpenApiQueryParam(
    override val name: String,
    override val required: Boolean? = null,
    override val deprecated: Boolean? = null,
    override val description: String? = null,
    override val schema: Referenceable<OpenApiSchema>? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiParameter() {
    override val in_ = ParameterLocation.query
}

@Suppress("unused")
data class OpenApiCookieParam(
    override val name: String,
    override val required: Boolean,
    override val deprecated: Boolean = false,
    override val description: String?,
    override val schema: Referenceable<OpenApiSchema>?,
    override val extensions: List<Extension> = emptyList()
) : OpenApiParameter() {
    override val in_ = ParameterLocation.cookie
}


data class HeaderName(val value: String) {
    override fun toString(): String = value
}

class OpenApiHeaders(
    x: Map<HeaderName, OpenApiHeader>,
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept(), Map<HeaderName, OpenApiHeader> by x {
}

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
    val headers: OpenApiHeaders? = null,
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept() {
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
    val security: Map<SecurityId, List<String>>? = null,
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept() {
    companion object {
        val empty = OpenApiOperation(OpenApiResponses())
    }
}

/**
 * Marker interface to make implementations easier to find
 */
interface SecurityRenderable : Renderable

data class SecurityId(val value: String) {
    override fun toString() = value
}

data class OpenApiSecurity(
    val id: SecurityId,
    val content: SecurityRenderable,
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept()

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
    val security: List<OpenApiSecurity>? = null,
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
) : OpenApiConcept()

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#licenseObject
 */
data class OpenApiLicense(
    val name: String,
    val url: String? = null,
    override val extensions: List<Extension> = emptyList()
) : OpenApiConcept()

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
    override val extensions: List<Extension> = emptyList()) : OpenApiConcept()

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
        val empty = OpenApiObject(OpenApiInfo("My api", "0.0"))
    }
}

/**
 * Used by lenses to add or make modifications to OpenApiRoute, but also
 * to idempotently add components like the security schemes and schemas used
 * in this route to the top-level OpenApi.
 *
 * So a single lens can for example both add a security method into
 * components/securitySchemes and add that scheme to a route
 */
data class OpenApiRouteInfo(
    val api: OpenApiObject,
    val route: OpenApiOperationInfo) {

    fun route(f: (OpenApiOperationInfo) -> OpenApiOperationInfo): OpenApiRouteInfo =
        this.copy(route = f(this.route))
}

/**
 * This is here in case we change our mind about the type of OpenApiRouteInfo
 */
fun info(api: OpenApiObject, route: OpenApiOperationInfo) = OpenApiRouteInfo(api, route)
