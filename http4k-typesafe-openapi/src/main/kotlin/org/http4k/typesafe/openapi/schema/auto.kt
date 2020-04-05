package org.http4k.typesafe.openapi.schema

import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.format.Json
import org.http4k.format.JsonLibAutoMarshallingJson
import org.http4k.typesafe.data.plus
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.json.Renderable
import org.http4k.typesafe.openapi.ForOpenApiLens
import org.http4k.typesafe.openapi.OpenApiBodyExample
import org.http4k.typesafe.openapi.OpenApiBodyExampleValue
import org.http4k.typesafe.openapi.OpenApiMediaType
import org.http4k.typesafe.openapi.OpenApiRequestBody
import org.http4k.typesafe.openapi.OpenApiResponse
import org.http4k.typesafe.openapi.OpenApiResponses
import org.http4k.typesafe.openapi.OpenApiSchema
import org.http4k.typesafe.openapi.Real
import org.http4k.typesafe.openapi.SchemaId
import org.http4k.typesafe.openapi.asOpenApi
import org.http4k.typesafe.openapi.fix
import org.http4k.typesafe.openapi.modifyComponents
import org.http4k.typesafe.openapi.openApiJson
import org.http4k.typesafe.openapi.real
import org.http4k.typesafe.openapi.requestBody
import org.http4k.typesafe.openapi.responses
import org.http4k.util.json.JsonToJsonSchema

inline fun <reified M : HttpMessage, NODE : Any> induceSchema(
    json: JsonLibAutoMarshallingJson<NODE>,
    crossinline example: Json<NODE>.() -> NODE): Kind2<ForOpenApiLens, M, NODE> =
    openApiJson(M::class, json).induceSchema(json, example)

/**
 * TODO: something has clearly gone wrong here
 */
class Hack(val node: Any) : Renderable {
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

/**
 * Guesses a json schema based on an example
 */
inline fun <reified M : HttpMessage, NODE : Any> Kind2<ForOpenApiLens, M, NODE>.induceSchema(
    json: JsonLibAutoMarshallingJson<NODE>,
    crossinline example: Json<NODE>.() -> NODE): Kind2<ForOpenApiLens, M, NODE> {

    val node = json.example()
    val schema = JsonToJsonSchema(json).toSchema(node)

    val mediaType = APPLICATION_JSON to OpenApiMediaType(
        mapOf("example" to OpenApiBodyExample(
            OpenApiBodyExampleValue.Real(node)
        )),
        OpenApiSchema.Raw(Hack(schema.node)).real()
    )
    val additionalSchemas: Iterable<Pair<SchemaId, OpenApiSchema>> = schema.definitions
        .map { (id, value) -> SchemaId(id) to OpenApiSchema.Raw(Hack(value)) }

    return this.fix()
        .asOpenApi {
            it.api { api ->
                api.modifyComponents {
                    this.schemas += additionalSchemas
                }
            }.let { info ->
                when (M::class) {
                    Request::class ->
                        info.requestBody {
                            when (val body = info.route.operation.requestBody) {
                                null -> {
                                    OpenApiRequestBody(
                                        "body",
                                        content = mapOf(mediaType),
                                        required = true).real()
                                }
                                is Real ->
                                    body.value.copy(
                                        content = body.value.content + mediaType,
                                        required = true
                                    ).real()
                                // TODO: handle refs
                                else -> body
                            }
                        }
                    Response::class ->
                        // TODO: what do we do if there are already responses?
                        info.responses {
                            OpenApiResponses(default = OpenApiResponse("body", mapOf(mediaType)).real())
                        }
                    else -> throw IllegalArgumentException("${M::class} was not a recognised HttpMessage type")
                }
            }
        }
}