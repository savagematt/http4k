package org.http4k.junit

import org.http4k.client.OkHttp
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.servirtium.InteractionOptions
import org.http4k.servirtium.InteractionOptions.Companion.Defaults
import org.http4k.servirtium.ServirtiumServer
import org.http4k.servirtium.StorageProvider
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class WithServirtiumRecordingServer(private val targetUri: Uri,
                                    private val storage: StorageProvider,
                                    private val baseName: String? = null,
                                    private val interactionOptions: InteractionOptions = Defaults) :
    TracksTestState by TracksTestState(),
    HasServirtiumServer,
    ParameterResolver {

    constructor(
        storage: StorageProvider,
        baseName: String? = null,
        interactionOptions: InteractionOptions = Defaults,
        port: Int = 0
    ) : this(Uri.of("http://localhost:$port"), storage, baseName, interactionOptions)

    override lateinit var control: ServirtiumServer

    override fun beforeEach(ec: ExtensionContext) {
        control = ServirtiumServer.Recording(
            (baseName?.let { "$it." } ?: "") + ec.testMethod.get().name,
            targetUri,
            storage,
            interactionOptions
        )
        super.beforeEach(ec)
    }

    override fun supportsParameter(pc: ParameterContext, ec: ExtensionContext) =
        pc.isHttpHandler() || pc.isInteractionControl()

    override fun resolveParameter(pc: ParameterContext, ec: ExtensionContext): Any =
        when {
            pc.isHttpHandler() -> ClientFilters.SetBaseUriFrom(if (inTest) control.uri() else targetUri)
                .then(OkHttp())
            else -> control
        }
}
