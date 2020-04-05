package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiOperationInfo

class OpenApiOperationInfoDsl(original: OpenApiOperationInfo) {
    var method = original.method
    var path = original.path
    var operation = OpenApiOperationDsl(original.operation)

    fun build() = OpenApiOperationInfo(
        method,
        path,
        operation.build()
    )

    operator fun invoke(f: OpenApiOperationInfoDsl.() -> Unit) = f(this)

    fun operation(f: OpenApiOperationDsl.() -> Unit) {
        f(operation)
    }
}