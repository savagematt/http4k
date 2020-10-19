package org.http4k.typesafe.openapi.eg

import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Uri
import org.http4k.typesafe.openapi.eg.contract.ErrorMessage
import org.http4k.typesafe.openapi.eg.contract.services.Service
import org.http4k.typesafe.openapi.eg.contract.services.ServiceId
import org.http4k.typesafe.openapi.eg.contract.services.servicesClient
import org.http4k.typesafe.openapi.eg.server.servicesServer
import org.http4k.typesafe.openapi.eg.util.random
import org.http4k.typesafe.openapi.routing.DebugRouteHandler
import org.junit.jupiter.api.Test

class ServicesTests {
    private val services = servicesClient(servicesServer(DebugRouteHandler()))

    @Test
    fun getNonExistent() {
        assertThat(services.get(::ServiceId.random()), equalTo(null as Service?))
    }

    @Test
    fun create() {
        val service = Service.random()
        services.create(service)
        assertThat(services.get(service.id), equalTo(service))
    }

    @Test
    fun createSameServiceTwice() {
        val service = Service.random()
        services.create(service)

        assertThat(
            services.create(service),
            equalTo(Failure(ErrorMessage("Service ${service.id} already exists")) as Result<Uri, ErrorMessage>))
    }

    @Test
    fun update() {
        val service = Service.random()
        services.create(service)
        assertThat(services.get(service.id), equalTo(service))
    }

    @Test
    fun updateNonExistentService() {
        val service = Service.random()
        assertThat(services.update(service),
            equalTo(Failure(ErrorMessage("Service ${service.id} does not exist")) as Result<Service, ErrorMessage>))
    }
}
