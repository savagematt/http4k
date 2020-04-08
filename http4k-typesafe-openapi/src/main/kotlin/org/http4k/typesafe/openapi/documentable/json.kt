package org.http4k.typesafe.openapi.documentable

import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.format.Json
import org.http4k.openapi.OpenApiBodyExample
import org.http4k.openapi.OpenApiBodyExampleValue
import org.http4k.openapi.OpenApiMediaType
import org.http4k.openapi.OpenApiSchema
import org.http4k.openapi.SchemaId
import org.http4k.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.openapi.real
import org.http4k.util.Renderable
import org.http4k.util.json.JsonSchema

/**
 * Returns an extension method that adds either a request or
 * a response to the openapi docs
 *
 * https://medium.com/tompee/idiomatic-kotlin-lambdas-with-receiver-and-dsl-3cd3348e1235
 */
inline fun <reified M : HttpMessage, NODE : Any> bodySchemaOf(
    example: NODE,
    schema: JsonSchema<NODE>): OpenApiRouteInfoDsl.() -> Unit {

    val mediaType = APPLICATION_JSON to OpenApiMediaType(
        mapOf("body" to OpenApiBodyExample(
            OpenApiBodyExampleValue.Real(example)
        )),
        OpenApiSchema.Raw(schema.node).real()
    )
    val additionalSchemas: Iterable<Pair<SchemaId, OpenApiSchema>> =
        schema.definitions
            .map { (id, value) -> SchemaId(id) to OpenApiSchema.Raw(value) }

    return {
        api {
            components {
                schemas += additionalSchemas
            }
        }
        route {
            operation {
                when (M::class) {
                    Request::class -> {
                        requestBody {
                            mapNullable {
                                mapReferenceable {
                                    content += mediaType
                                    required = true
                                }
                            }
                        }
                    }
                    Response::class -> {
                        responses {
                            default {
                                mapNullable {
                                    mapReferenceable {
                                        content += mediaType
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}