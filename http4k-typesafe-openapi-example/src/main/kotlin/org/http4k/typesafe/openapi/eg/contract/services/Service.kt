package org.http4k.typesafe.openapi.eg.contract.services

import org.http4k.typesafe.openapi.eg.HasId
import org.http4k.typesafe.openapi.eg.Value
import org.http4k.typesafe.openapi.eg.util.random
import java.util.*

class ServiceId(value: UUID) : Value<UUID>(value){
    companion object {
        val example = ServiceId(UUID.fromString("513f83e4-bf04-48e0-8e27-b17f87a25391"))
    }
}

data class Service(override val id: ServiceId, val name: String) : HasId<ServiceId> {
    class Builder(base: Service) {
        var id = base.id
        var name = base.name
        fun build() = Service(id, name)
    }

    companion object {
        fun random(fn: Builder.() -> Unit = {}) =
            Builder(Service(::ServiceId.random(), random("Service")))
                .also(fn)
                .build()
    }
}