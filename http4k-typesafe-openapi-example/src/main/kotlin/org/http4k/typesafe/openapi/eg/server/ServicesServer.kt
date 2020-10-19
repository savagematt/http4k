package org.http4k.typesafe.openapi.eg.server

import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.Success
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.typesafe.openapi.eg.contract.ErrorMessage
import org.http4k.typesafe.openapi.eg.contract.services.Service
import org.http4k.typesafe.openapi.eg.contract.services.ServiceId
import org.http4k.typesafe.openapi.eg.contract.services.ServicesApi
import org.http4k.typesafe.openapi.eg.contract.services.ServicesRoutes
import org.http4k.typesafe.openapi.eg.util.ThisShouldNeverHappen
import org.http4k.typesafe.openapi.routing.server
import org.http4k.typesafe.routing.RouteHandler
import org.http4k.typesafe.routing.Router
import org.http4k.typesafe.routing.uri
import org.http4k.util.data.Tuple2
import kotlin.collections.set

class Services(val routes: ServicesRoutes, val state: HashMap<ServiceId, Service>) : ServicesApi {

    override fun get(id: ServiceId): Service? =
        state[id]

    override fun create(value: Service): Result<Uri, ErrorMessage> =
        when {
            state.containsKey(value.id) -> {
                Failure(ErrorMessage("Service ${value.id} already exists"))
            }
            else -> {
                val result = when (val uri = routes.get.request.uri(value.id)) {
                    is Failure -> throw ThisShouldNeverHappen("Get service uri could not be created")
                    is Success -> uri
                }
                state[value.id] = value
                result
            }
        }

    override fun update(value: Service): Result<Service, ErrorMessage> =
        when {
            state.containsKey(value.id) -> {
                Success(value.also { state.put(value.id, value) })
            }
            else -> {
                Failure(ErrorMessage("Service ${value.id} does not exist"))
            }
        }

}

fun servicesServer(handler: RouteHandler<OpenApiRouteDocs>): HttpHandler {
    val routes = ServicesRoutes()
    val behaviour = Services(routes, HashMap())

    return Router(
        handler,
        routes.get server behaviour::get,
        routes.create server behaviour::create,
        routes.update server { args: Tuple2<ServiceId, Service> ->
            when (args.a) {
                args.b.id -> behaviour.update(args.b)
                else -> throw IllegalArgumentException("Service id in url did not match payload")
            }
        }
    )
}