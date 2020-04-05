package org.http4k.typesafe.openapi

import org.http4k.format.Json
import org.http4k.typesafe.data.checkUnique


class V3Renderer<NODE>(
    val json: Json<NODE>,
    private val valueRenderer: JsonRenderer<Any, NODE>)
    : OpenApiRenderer<NODE>, Json<NODE> by json {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun render(concept: OpenApiConcept): NODE =
        when (concept) {
            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#tagObject
            is Tag -> obj(
                "name" to string(concept.name),
                "description" to nullable(concept.description)
            )

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#referenceObject
            is Ref<*> -> obj(
                "\$ref" to string(concept.value)
            )

            is Referenceable<*> -> when (concept) {
                is Ref<*> -> render(concept)
                is Real<*> -> render(concept.value)
            }

            //https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#schemaObject
            is OpenApiSchema -> nullNode()

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#exampleObject
            is OpenApiBodyExampleValue -> when (concept) {
                is OpenApiBodyExampleValue.External -> string(concept.value)
                is OpenApiBodyExampleValue.Real -> valueRenderer.render(concept.value)
            }

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#exampleObject
            is OpenApiBodyExample -> obj(
                listOf(
                    "summary" to nullable(concept.summary),
                    "description" to nullable(concept.summary)) +
                    when (concept.value) {
                        is OpenApiBodyExampleValue.External -> "value" to render(concept.value)
                        is OpenApiBodyExampleValue.Real -> "externalValue" to render(concept.value)
                    }
            )
            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#mediaTypeObject
            is OpenApiMediaType -> obj(
                "schema" to nullable(concept.schema),
                "examples" to obj(concept.examples.map { it.key to render(it.value) })
            )

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#requestBodyObject
            is OpenApiRequestBody -> obj(
                "description" to nullable(concept.description),
                "content" to nullableObj(concept.content) { it.value },
                "required" to nullable(concept.required)
            )

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#parameterObject
            is OpenApiParameter -> obj(
                "name" to string(concept.name),
                "in" to string(concept.in_.name.toLowerCase()),
                "description" to nullable(concept.description),
                "required" to nullable(concept.required),
                "deprecated" to nullable(concept.deprecated),
                "schema" to nullable(concept.schema)
            )

            is OpenApiHeaders -> obj(concept)

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#responseObject
            is OpenApiResponse -> obj(
                "description" to nullable(concept.description),
                "content" to nullableObj(concept.content) { it.value },
                "headers" to nullableObj(concept.headers)
            )

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#responsesObject
            is OpenApiResponses -> {
                val statuses = concept.byStatus?.map { it.key.code.toString() to render(it.value) }
                obj(
                    listOf("default" to nullable(concept.default))
                        + (statuses ?: emptyList())
                )
            }

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#operationObject
            is OpenApiOperation -> obj(
                "responses" to render(concept.responses),
                "tags" to nullable(concept.tags),
                "summary" to nullable(concept.summary),
                "description" to nullable(concept.description),
                "operationId" to nullable(concept.operationId),
                "parameters" to nullable(concept.parameters),
                "requestBody" to nullable(concept.requestBody),
                "deprecated" to nullable(concept.deprecated),
                "security" to nullable(concept.security, { security ->
                    obj(security.entries.map { entry ->
                        entry.key.value to this.array(entry.value.map { string(it) })
                    })
                })
            )

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#securityRequirementObject
            is OpenApiSecurity -> concept.content.render(this)

            is OpenApiOperationInfo -> render(concept.operation)

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#componentsObject
            is OpenApiComponents -> obj(
                "schemas" to nullableObj(concept.schemas)
            )

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#contactObject
            is OpenApiContact -> obj(
                "name" to nullable(concept.name),
                "url" to nullable(concept.url),
                "email" to nullable(concept.email)
            )

            //  https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#licenseObject
            is OpenApiLicense -> obj(
                "name" to string(concept.name),
                "url" to nullable(concept.url)
            )

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#infoObject
            is OpenApiInfo -> obj(
                "title" to string(concept.title),
                "version" to string(concept.version),
                "description" to nullable(concept.description),
                "termsOfService" to nullable(concept.termsOfService),
                "contact" to nullable(concept.contact),
                "license" to nullable(concept.license)
            )

            // https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#openapi-object
            is OpenApiObject -> obj(
                "openapi" to string("3.0.3"),
                "info" to render(concept.info),
                "paths" to paths(concept.paths),
                "components" to render(concept.components)
            )
        }
}

fun <NODE> V3Renderer<NODE>.paths(paths: List<OpenApiOperationInfo>): NODE {
    val byPath = paths
        .fold(emptyMap<String, List<OpenApiOperationInfo>>()) { routesByPath, route ->
            val k = route.path
            routesByPath + (k to ((routesByPath[k] ?: emptyList()) + route))
        }

    return obj(byPath.entries.toList()
        .map { (path, routes) ->
            val methods = routes.map { route -> route.method.name to render(route) }
            path to obj(
                methods.checkUnique({ it.first },
                    { method ->
                        throw IllegalStateException(
                            "Path '$path' has duplicate operations for method $method")
                    }))
        })
}
