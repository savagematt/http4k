package org.http4k.openapi.builders

import org.http4k.openapi.OpenApiOperationInfo
import org.http4k.util.builders.BaseBuilder

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