package org.http4k.typesafe.openapi.schema

import com.natpryce.Success
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.format.Json
import org.http4k.format.JsonLibAutoMarshallingJson
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.json.Renderable
import org.http4k.typesafe.openapi.ForOpenApiLens
import org.http4k.typesafe.openapi.OpenApiBodyExample
import org.http4k.typesafe.openapi.OpenApiBodyExampleValue
import org.http4k.typesafe.openapi.OpenApiLens
import org.http4k.typesafe.openapi.OpenApiMediaType
import org.http4k.typesafe.openapi.OpenApiSchema
import org.http4k.typesafe.openapi.SchemaId
import org.http4k.typesafe.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.typesafe.openapi.openapi
import org.http4k.typesafe.openapi.real
import org.http4k.typesafe.openapi.routing.OpenApiRouting.map
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.http4k.typesafe.routing.messages.body.JsonLens
import org.http4k.util.json.AutoJsonToJsonSchema
import org.http4k.util.json.JsonSchema
import org.http4k.util.json.JsonToJsonSchema

/**
 * Guesses a json schema for request or response body, based on an example, and
 * adds it to the openapi docs.
 *
 * Parses/writes json from/to the message body, and then uses [JsonLibAutoMarshallingJson]
 * to deserialize/serialise a value of type [T].
 */
inline fun <reified M : HttpMessage, reified NODE : Any, reified T : Any> JsonLibAutoMarshallingJson<NODE>.typed(
    example: T,
    schemaId: SchemaId? = null
): Kind2<ForOpenApiLens, M, T> {

    val schema = AutoJsonToJsonSchema(this).toSchema(example, schemaId?.value)

    return this.jsonSchema<M, NODE>(
        asJsonObject(example),
        schema)
        .map(
            {
                try {
                    Success(this.asA(it, T::class))
                } catch (e: Exception) {
                    routeFailed(BAD_REQUEST, e.message ?: "Deserialisation failed")
                }
            },
            { this.asJsonObject(it) }
        )
}

/**
 * Guesses a json schema for request or response body, based on an example, and
 * adds it to the openapi docs
 */
inline fun <reified M : HttpMessage, NODE : Any> JsonLibAutoMarshallingJson<NODE>.plain(
    example: NODE,
    schemaId: SchemaId? = null
): Kind2<ForOpenApiLens, M, NODE> {
    return jsonSchema(
        example,
        JsonToJsonSchema(this).toSchema(example, schemaId?.value))
}

inline fun <reified M : HttpMessage, NODE : Any> JsonLibAutoMarshallingJson<NODE>.jsonSchema(example: NODE, schema: JsonSchema<NODE>): OpenApiLens<M, NODE> {
    return JsonLens<M, NODE>(this) openapi documentBody<M, NODE>(
        example,
        schema)
}

/**
 * Returns an extension method that adds either a request or
 * a response to the openapi docs
 */
inline fun <reified M : HttpMessage, NODE : Any> documentBody(
    example: NODE,
    schema: JsonSchema<NODE>): OpenApiRouteInfoDsl.() -> Unit {

    val mediaType = APPLICATION_JSON to OpenApiMediaType(
        mapOf("example" to OpenApiBodyExample(
            OpenApiBodyExampleValue.Real(example)
        )),
        OpenApiSchema.Raw(Hack(schema.node)).real()
    )
    val additionalSchemas: Iterable<Pair<SchemaId, OpenApiSchema>> =
        schema.definitions
            .map { (id, value) -> SchemaId(id) to OpenApiSchema.Raw(Hack(value)) }

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
