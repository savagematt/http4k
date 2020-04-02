package org.http4k.typesafe.openapi

import org.http4k.core.Method
import org.http4k.core.Status


/**
 * Something that is capable of describing itself in some sort of
 * document
 */
interface Documentable<T> {
    fun document(doc: T): T
}

// byStatus

fun OpenApiResponses.byStatus(
    f: (Map<Status, Referenceable<OpenApiResponse>>) -> Map<Status, Referenceable<OpenApiResponse>>)
    : OpenApiResponses =
    this.copy(byStatus = f(this.byStatus ?: emptyMap()))

fun OpenApiOperation.byStatus(
    f: (Map<Status, Referenceable<OpenApiResponse>>) -> Map<Status, Referenceable<OpenApiResponse>>)
    : OpenApiOperation =
    this.copy(responses = this.responses.byStatus(f))


// parameters
fun OpenApiOperation.parameters(f: (List<Referenceable<OpenApiParameter>>) -> List<Referenceable<OpenApiParameter>>): OpenApiOperation =
    this.copy(parameters = f(this.parameters ?: emptyList()))

fun OpenApiOperationInfo.parameters(f: (List<Referenceable<OpenApiParameter>>) -> List<Referenceable<OpenApiParameter>>): OpenApiOperationInfo =
    this.copy(operation = this.operation.parameters(f))

fun OpenApiRouteInfo.parameters(f: (List<Referenceable<OpenApiParameter>>) -> List<Referenceable<OpenApiParameter>>): OpenApiRouteInfo =
    this.copy(route = route.parameters(f))

// operation

fun OpenApiOperationInfo.operation(f: (OpenApiOperation) -> OpenApiOperation): OpenApiOperationInfo =
    this.copy(operation = f(this.operation))

fun OpenApiRouteInfo.operation(f: (OpenApiOperation) -> OpenApiOperation):
    OpenApiRouteInfo =
    this.copy(route = route.operation(f))

// responses

fun OpenApiOperation.responses(f: (OpenApiResponses) -> OpenApiResponses) =
    this.copy(responses = f(this.responses))

fun OpenApiOperationInfo.responses(f: (OpenApiResponses) -> OpenApiResponses) =
    this.copy(operation = this.operation.responses(f))

fun OpenApiRouteInfo.responses(f: (OpenApiResponses) -> OpenApiResponses) =
    this.copy(route = this.route.responses(f))

// requestBody

fun OpenApiOperation.requestBody(f: (Referenceable<OpenApiRequestBody>?) -> Referenceable<OpenApiRequestBody>?) =
    this.copy(requestBody = f(this.requestBody))

fun OpenApiOperationInfo.requestBody(f: (Referenceable<OpenApiRequestBody>?) -> Referenceable<OpenApiRequestBody>?) =
    this.copy(operation = this.operation.requestBody(f))

fun OpenApiRouteInfo.requestBody(f: (Referenceable<OpenApiRequestBody>?) -> Referenceable<OpenApiRequestBody>?) =
    this.copy(route = this.route.requestBody(f))

// method

fun OpenApiOperationInfo.method(f: (Method) -> Method) =
    this.copy(method = f(this.method))

fun OpenApiRouteInfo.method(f: (Method) -> Method) =
    this.copy(route = this.route.method(f))