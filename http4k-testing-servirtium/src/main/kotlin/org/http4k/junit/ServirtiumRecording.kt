package org.http4k.junit

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.TrafficFilters
import org.http4k.servirtium.InteractionControl
import org.http4k.servirtium.InteractionOptions
import org.http4k.servirtium.StorageProvider
import org.http4k.servirtium.trafficPrinter
import org.http4k.traffic.Servirtium
import org.http4k.traffic.Sink
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

/**
 * JUnit 5 extension for recording HTTP traffic in Servirtium format.
 */
class ServirtiumRecording(
    private val baseName: String,
    private val httpHandler: HttpHandler,
    private val storageProvider: StorageProvider,
    private val options: InteractionOptions = InteractionOptions.Companion.Defaults)
    : TracksTestState by TracksTestState(), ParameterResolver {
    override fun supportsParameter(pc: ParameterContext, ec: ExtensionContext) = pc.isHttpHandler() || pc.isInteractionControl()

    override fun resolveParameter(pc: ParameterContext, ec: ExtensionContext): Any = with(ec.testInstance.get()) {
        val testName = "$baseName.${ec.requiredTestMethod.name}"
        val storage = storageProvider(testName).apply { clean() }
        if (pc.isHttpHandler()) {
            when {
                inTest -> TrafficFilters.RecordTo(Sink.Servirtium(storage, options))
                    .then(options.trafficPrinter())
                    .then(httpHandler)
                else -> httpHandler
            }
        } else InteractionControl.StorageBased(storage)
    }
}
