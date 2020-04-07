package org.http4k.typesafe.openapi.builders

import org.http4k.typesafe.openapi.OpenApiOperationInfo

class OpenApiOperationInfoDsl(original: OpenApiOperationInfo)
    : BaseBuilder<OpenApiOperationInfo, OpenApiOperationInfoDsl>(::OpenApiOperationInfoDsl) {
    var method = original.method
    var path = original.path
    var operation = OpenApiOperationDsl(original.operation)

    override operator fun invoke(f: OpenApiOperationInfoDsl.() -> Unit) = f(this)

    override fun build() = OpenApiOperationInfo(
        method,
        path,
        operation.build()
    )
}