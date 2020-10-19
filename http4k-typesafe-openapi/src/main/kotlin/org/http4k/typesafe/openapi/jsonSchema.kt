package org.http4k.typesafe.openapi

import com.natpryce.Success
import org.http4k.core.HttpMessage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.format.JsonLibAutoMarshallingJson
import org.http4k.openapi.SchemaId
import org.http4k.typesafe.openapi.documentable.bodySchemaOf
import org.http4k.typesafe.openapi.routing.map
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.http4k.typesafe.routing.messages.body.JsonLens
import org.http4k.util.json.AutoJsonToJsonSchema
import org.http4k.util.json.JsonSchema
import org.http4k.util.json.JsonToJsonSchema

inline fun <reified NODE : Any, reified T : Any> JsonLibAutoMarshallingJson<NODE>.request(
    example: T
): OpenApiLens<Request, T> =
    this.typed(null, example)

inline fun <reified NODE : Any, reified T : Any> JsonLibAutoMarshallingJson<NODE>.response(
    example: T
): OpenApiLens<Response, T> =
    this.typed(null, example)

inline fun <reified M : HttpMessage, reified NODE : Any, reified T : Any> JsonLibAutoMarshallingJson<NODE>.typed(
    example: T
): OpenApiLens<M, T> =
    this.typed(null, example)

/**
 * Guesses a json schema for request or response body, based on an example, and
 * adds it to the openapi docs.
 *
 * Parses/writes json from/to the message body, and then uses [JsonLibAutoMarshallingJson]
 * to deserialize/serialise a value of type [T].
 */
inline fun <reified M : HttpMessage, reified NODE : Any, reified T : Any>
    JsonLibAutoMarshallingJson<NODE>.typed(
    schemaId: SchemaId?,
    example: T)
    : OpenApiLens<M, T> {

    val schema = AutoJsonToJsonSchema(this).toSchema(example, schemaId?.value)
    val clazz = T::class

    return this.jsonSchema<M, NODE>(
        asJsonObject(example),
        schema)
        .map(
            {
                try {
                    Success(this.asA(it, clazz))
                } catch (e: Exception) {
                    routeFailed(
                        e.message?.let { "Json deserialization failed: ${it}" }
                            ?: "Deserialization failed",
                        e,
                        Response(BAD_REQUEST))
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
    schemaId: SchemaId? = null): OpenApiLens<M, NODE> {
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
    exampleBuilder: JsonLibAutoMarshallingJson<NODE>.() -> NODE): OpenApiLens<M, NODE> {
    return this.plain(exampleBuilder(this), schemaId)
}

inline fun <reified M : HttpMessage, NODE : Any> JsonLibAutoMarshallingJson<NODE>.jsonSchema(
    example: NODE,
    schema: JsonSchema<NODE>): OpenApiLens<M, NODE> {

    return JsonLens<M, NODE>(this) openapi bodySchemaOf<M, NODE>(
        example,
        schema)
}