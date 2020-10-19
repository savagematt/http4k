package org.http4k.typesafe.openapi.eg.contract.services

import com.natpryce.Result
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.Uri.Companion.of
import org.http4k.typesafe.openapi.OpenApiPath
import org.http4k.typesafe.openapi.OpenApiRoute
import org.http4k.typesafe.openapi.eg.contract.ErrorMessage
import org.http4k.typesafe.openapi.eg.contract.errorMessage
import org.http4k.typesafe.openapi.eg.contract.json
import org.http4k.typesafe.openapi.eg.contract.services.ServicesPaths.serviceId
import org.http4k.typesafe.openapi.eg.string
import org.http4k.typesafe.openapi.eg.util.correctId
import org.http4k.typesafe.openapi.request
import org.http4k.typesafe.openapi.response
import org.http4k.typesafe.openapi.routing.at
import org.http4k.typesafe.openapi.routing.call
import org.http4k.typesafe.openapi.routing.div
import org.http4k.typesafe.openapi.routing.map
import org.http4k.typesafe.openapi.routing.or
import org.http4k.typesafe.openapi.routing.pathVar
import org.http4k.typesafe.openapi.routing.required
import org.http4k.typesafe.openapi.routing.response
import org.http4k.typesafe.openapi.routing.result
import org.http4k.typesafe.openapi.routing.uuid
import org.http4k.typesafe.openapi.routing.with
import org.http4k.typesafe.routing.Route
import org.http4k.util.data.Tuple2
import org.http4k.util.data.tuple

object ServicesPaths {
    val serviceId: OpenApiPath<ServiceId> = pathVar("serviceId").uuid().map({ ServiceId(it) }, { it.value })
}

interface ServicesApi {
    fun get(id: ServiceId): Service?
    fun create(value: Service): Result<Uri, ErrorMessage>
    fun update(value: Service): Result<Service, ErrorMessage>
}


class ServicesRoutes() {
    val get: OpenApiRoute<ServiceId, Service?> = Route(
        GET at "/services" / serviceId,

        OK with json.response(Service(ServiceId.example, "Service Repository"))
            or NOT_FOUND
    )

    val create: OpenApiRoute<Service, Result<Uri, ErrorMessage>> = Route(
        POST at "/services" / serviceId
            with json.request(Service(ServiceId.example, "Service Registry"))
            with correctId<ServiceId, Service>(),

        result(
            CREATED with response.header("Location").required().string { of(it) },
            BAD_REQUEST with errorMessage()
        )
    )

    val update: OpenApiRoute<Tuple2<ServiceId, Service>, Result<Service, ErrorMessage>> = Route(
        PUT at "/services" / serviceId
            with json.request(Service(ServiceId.example, "Service Repository")),

        result(
            OK with json.response(Service(ServiceId.example, "Service Repository")),
            NOT_FOUND with errorMessage()
        )
    )
}


fun servicesClient(http: HttpHandler): ServicesApi {
    val routes = ServicesRoutes()

    return object : ServicesApi {
        override fun get(id: ServiceId): Service? =
            routes.get(http, id)

        override fun create(value: Service): Result<Uri, ErrorMessage> =
            routes.create(http, value)

        override fun update(value: Service): Result<Service, ErrorMessage> =
            routes.update(http, tuple(value.id, value))
    }
}
