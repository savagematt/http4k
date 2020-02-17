package org.http4k.junit

import org.http4k.client.OkHttp
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Status.Companion.NOT_IMPLEMENTED
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.filter.TrafficFilters.RecordTo
import org.http4k.server.ServerConfig
import org.http4k.server.SunHttp
import org.http4k.servirtium.InteractionControl
import org.http4k.servirtium.InteractionControl.Companion.NoOp
import org.http4k.servirtium.InteractionOptions
import org.http4k.servirtium.InteractionOptions.Companion.Defaults
import org.http4k.servirtium.ServirtiumServer
import org.http4k.servirtium.StorageProvider
import org.http4k.servirtium.trafficPrinter
import org.http4k.traffic.Replay
import org.http4k.traffic.Servirtium
import org.http4k.traffic.Sink
import org.http4k.traffic.replayingMatchingContent
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.opentest4j.AssertionFailedError

class ServirtiumRecording(
    private val baseName: String,
    private val httpHandler: HttpHandler,
    private val storageProvider: StorageProvider,
    private val options: InteractionOptions = Defaults)
    : TrackTestState by TrackTestState(), ParameterResolver {
    override fun supportsParameter(pc: ParameterContext, ec: ExtensionContext) = pc.isHttpHandler() || pc.isRecordingControl()

    override fun resolveParameter(pc: ParameterContext, ec: ExtensionContext): Any = with(ec.testInstance.get()) {
        val testName = "$baseName.${ec.requiredTestMethod.name}"
        val storage = storageProvider(testName).apply { clean() }
        if (pc.isHttpHandler()) {
            when {
                inTest -> RecordTo(Sink.Servirtium(storage, options))
                    .then(options.trafficPrinter())
                    .then(httpHandler)
                else -> httpHandler
            }
        } else InteractionControl.StorageBased(storage)
    }
}

/**
 * JUnit 5 extension for replaying HTTP traffic from disk in Servirtium format.
 */
class ServirtiumReplay(private val baseName: String,
                       private val storageProvider: StorageProvider,
                       private val options: InteractionOptions = Defaults) : ParameterResolver {
    override fun supportsParameter(pc: ParameterContext, ec: ExtensionContext) = pc.isHttpHandler() || pc.isRecordingControl()

    override fun resolveParameter(pc: ParameterContext, ec: ExtensionContext): Any =
        if (pc.isHttpHandler()) {
            ConvertBadResponseToAssertionFailed()
                .then(Replay.Servirtium(storageProvider("$baseName.${ec.requiredTestMethod.name}"), options)
                    .replayingMatchingContent(options::modify)
                )
        } else NoOp
}

private fun ConvertBadResponseToAssertionFailed() = Filter { next ->
    {
        with(next(it)) {
            if (status == NOT_IMPLEMENTED) throw AssertionFailedError(bodyString())
            this
        }
    }
}

private fun ParameterContext.isRecordingControl() =
    parameter.parameterizedType.typeName == InteractionControl::class.java.name

private fun ParameterContext.isHttpHandler() =
    parameter.parameterizedType.typeName == "kotlin.jvm.functions.Function1<? super org.http4k.core.Request, ? extends org.http4k.core.Response>"


class StartServirtiumRecordingServer(private val targetUri: Uri,
                                     private val storage: StorageProvider,
                                     private val baseName: String? = null,
                                     private val interactionOptions: InteractionOptions = Defaults) :
    TrackTestState by TrackTestState(),
    ParameterResolver,
    BeforeEachCallback,
    AfterEachCallback {

    constructor(port: Int,
                storage: StorageProvider,
                baseName: String? = null,
                interactionOptions: InteractionOptions = Defaults) : this(
        Uri.of("http://localhost:$port"), storage, baseName, interactionOptions
    )

    private lateinit var control: ServirtiumServer

    fun control() = control

    override fun beforeEach(context: ExtensionContext) {
        control = ServirtiumServer.Recording(
            baseName?.let { "$it." } + context.testMethod.get().name,
            targetUri,
            storage,
            interactionOptions
        )
        control.start()
    }

    override fun afterEach(context: ExtensionContext) {
        control.stop()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext) =
        parameterContext.isHttpHandler() || parameterContext.isRecordingControl()

    override fun resolveParameter(pc: ParameterContext, ec: ExtensionContext): Any =
        if (pc.isHttpHandler())
            ClientFilters.SetBaseUriFrom(if (inTest) control.uri() else targetUri)
                .then(OkHttp())
        else control
}

class StartServirtiumReplayingServer(private val port: Int,
                                     private val storage: StorageProvider,
                                     private val baseName: String? = null,
                                     private val interactionOptions: InteractionOptions = Defaults,
                                     private val serverFn: (Int) -> ServerConfig = ::SunHttp) :
    ParameterResolver, BeforeEachCallback, AfterEachCallback {

    private lateinit var control: ServirtiumServer

    override fun beforeEach(context: ExtensionContext) {
        control = ServirtiumServer.Replay(
            baseName?.let { "$it." } + context.testMethod.get().name,
            storage,
            interactionOptions,
            port,
            serverFn
        )
        control.start()
    }

    override fun afterEach(context: ExtensionContext) {
        control.stop()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext) =
        parameterContext.isHttpHandler()

    override fun resolveParameter(pc: ParameterContext, ec: ExtensionContext): Any =
        ClientFilters.SetBaseUriFrom(Uri.of("http://localhost:${control.port()}"))
            .then(OkHttp())
}
