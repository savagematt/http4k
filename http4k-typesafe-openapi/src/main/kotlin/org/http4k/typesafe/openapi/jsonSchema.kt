package org.http4k.typesafe.openapi

import com.natpryce.Success
import org.http4k.core.HttpMessage
import org.http4k.core.Status
import org.http4k.format.JsonLibAutoMarshallingJson
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.openapi.documentable.bodySchemaOf
import org.http4k.typesafe.openapi.routing.OpenApiRouting.map
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.messages.body.JsonLens
import org.http4k.util.json.AutoJsonToJsonSchema
import org.http4k.util.json.JsonSchema
import org.http4k.util.json.JsonToJsonSchema

inline fun <reified M : HttpMessage, reified NODE : Any, reified T : Any> JsonLibAutoMarshallingJson<NODE>.typed(
    example: T
): Kind2<ForOpenApiLens, M, T>  =
    this.typed(null, example)

/**
 * Guesses a json schema for request or response body, based on an example, and
 * adds it to the openapi docs.
 *
 * Parses/writes json from/to the message body, and then uses [JsonLibAutoMarshallingJson]
 * to deserialize/serialise a value of type [T].
 */
inline fun <reified M : HttpMessage, reified NODE : Any, reified T : Any> JsonLibAutoMarshallingJson<NODE>.typed(
    schemaId: SchemaId?,
    example: T
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
                    RoutingError.routeFailed(Status.BAD_REQUEST, e.message
                        ?: "Deserialisation failed")
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
    schemaId: SchemaId? = null): Kind2<ForOpenApiLens, M, NODE> {
    return jsonSchema(
        example,
        JsonToJsonSchema(this).toSchema(example, schemaId?.value))
}

/**
 * Guesses a json schema for request or response body, based on an example, and
 * adds it to the openapi docs
 */
inline fun <reified M : HttpMessage, NODE : Any> JsonLibAutoMarshallingJson<NODE>.plain(
    schemaId: SchemaId? = null,
    exampleBuilder: JsonLibAutoMarshallingJson<NODE>.() -> NODE): Kind2<ForOpenApiLens, M, NODE> {
    return this.plain(exampleBuilder(this), schemaId)}

inline fun <reified M : HttpMessage, NODE : Any> JsonLibAutoMarshallingJson<NODE>.jsonSchema(
    example: NODE,
    schema: JsonSchema<NODE>): OpenApiLens<M, NODE> {

    return JsonLens<M, NODE>(this) openapi bodySchemaOf<M, NODE>(
        example,
        schema)
}