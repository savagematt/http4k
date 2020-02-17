package org.http4k.junit

import org.http4k.core.Filter
import org.http4k.core.Status.Companion.NOT_IMPLEMENTED
import org.http4k.servirtium.InteractionControl
import org.junit.jupiter.api.extension.ParameterContext
import org.opentest4j.AssertionFailedError

internal fun ConvertBadResponseToAssertionFailed() = Filter { next ->
    {
        with(next(it)) {
            if (status == NOT_IMPLEMENTED) throw AssertionFailedError(bodyString())
            this
        }
    }
}

internal fun ParameterContext.isInteractionControl() =
    parameter.parameterizedType.typeName == InteractionControl::class.java.name

internal fun ParameterContext.isHttpHandler() =
    parameter.parameterizedType.typeName == "kotlin.jvm.functions.Function1<? super org.http4k.core.Request, ? extends org.http4k.core.Response>"

