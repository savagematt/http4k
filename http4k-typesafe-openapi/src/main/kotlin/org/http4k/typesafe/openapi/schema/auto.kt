package org.http4k.typesafe.openapi.schema

import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.format.Json
import org.http4k.format.JsonLibAutoMarshallingJson
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.json.Renderable
import org.http4k.typesafe.openapi.ForOpenApiLens
import org.http4k.typesafe.openapi.OpenApiBodyExample
import org.http4k.typesafe.openapi.OpenApiBodyExampleValue
import org.http4k.typesafe.openapi.OpenApiMediaType
import org.http4k.typesafe.openapi.OpenApiSchema
import org.http4k.typesafe.openapi.SchemaId
import org.http4k.typesafe.openapi.fix
import org.http4k.typesafe.openapi.openApiJson
import org.http4k.typesafe.openapi.openapi
import org.http4k.typesafe.openapi.real
import org.http4k.util.json.JsonToJsonSchema

/**
 * Guesses a json schema for request or response body, based on an example
 */
inline fun <reified M : HttpMessage, NODE : Any> induce(
    json: JsonLibAutoMarshallingJson<NODE>,
    schemaId: SchemaId? = null,
    crossinline example: Json<NODE>.() -> NODE
): Kind2<ForOpenApiLens, M, NODE> =
    openApiJson(M::class, json).induce(json, schemaId, example)

/**
 * Guesses a json schema for request or response body, based on an example
 */
inline fun <reified M : HttpMessage, NODE : Any> Kind2<ForOpenApiLens, M, NODE>.induce(
    json: JsonLibAutoMarshallingJson<NODE>,
    schemaId: SchemaId? = null,
    crossinline example: Json<NODE>.() -> NODE): Kind2<ForOpenApiLens, M, NODE> {

    val node = json.example()
    val schema = JsonToJsonSchema(json).toSchema(node, schemaId?.value)

    val mediaType = APPLICATION_JSON to OpenApiMediaType(
        mapOf("example" to OpenApiBodyExample(
            OpenApiBodyExampleValue.Real(node)
        )),
        OpenApiSchema.Raw(Hack(schema.node)).real()
    )
    val additionalSchemas: Iterable<Pair<SchemaId, OpenApiSchema>> =
        schema.definitions
            .map { (id, value) -> SchemaId(id) to OpenApiSchema.Raw(Hack(value)) }

    return this.fix() openapi {
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
//            }.let { info ->
//                when (M::class) {
//                    Request::class ->
//                        info.requestBody {
//                            when (val body = info.route.operation.requestBody) {
//                                null -> {
//                                    OpenApiRequestBody(
//                                        "body",
//                                        content = mapOf(mediaType),
//                                        required = true).real()
//                                }
//                                is Real ->
//                                    body.value.copy(
//                                        content = body.value.content + mediaType,
//                                        required = true
//                                    ).real()
//                                // TODO: handle refs
//                                else -> body
//                            }
//                        }
//                    Response::class ->
//                        // TODO: what do we do if there are already responses?
//                        info.responses {
//                            OpenApiResponses(default = OpenApiResponse("body", mapOf(mediaType)).real())
//                        }
//                    else -> throw IllegalArgumentException("${M::class} was not a recognised HttpMessage type")
//                }
//            }
//        } ?: {})
}


/**
 * TODO: something has clearly gone wrong here
 *
 * We have to pass Json<NODE_1> into JsonLens, because it needs to have it to hand
 * immediately when we call get(), and
 */
class Hack(private val node: Any) : Renderable {
    override fun <NODE> render(json: Json<NODE>): NODE {
        try {
            @Suppress("UNCHECKED_CAST")
            return node as NODE
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "Sadly this ${node::class} cannot be rendered with ${json::class}. " +
                    "You need to use the same Json when defining your routes and rendering to openapi.")
        }
    }

}