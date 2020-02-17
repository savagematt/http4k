package org.http4k.junit

import org.http4k.client.OkHttp
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.server.ServerConfig
import org.http4k.server.SunHttp
import org.http4k.servirtium.InteractionControl.Companion.NoOp
import org.http4k.servirtium.InteractionOptions
import org.http4k.servirtium.InteractionOptions.Companion.Defaults
import org.http4k.servirtium.ServirtiumServer
import org.http4k.servirtium.StorageProvider
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class WithServirtiumReplayServer(
    private val baseName: String,
    private val storage: StorageProvider,
    private val interactionOptions: InteractionOptions = Defaults,
    private val port: Int = 0,
    private val serverFn: (Int) -> ServerConfig = ::SunHttp) :
    ParameterResolver, HasServirtiumServer {

    override lateinit var control: ServirtiumServer

    override fun beforeEach(ec: ExtensionContext) {
        control = ServirtiumServer.Replay(
            baseName + "." + ec.testMethod.get().name,
            storage,
            interactionOptions,
            port,
            serverFn
        )
        super.beforeEach(ec)
    }

    override fun supportsParameter(pc: ParameterContext, ec: ExtensionContext) =
        pc.isHttpHandler() || pc.isInteractionControl()

    override fun resolveParameter(pc: ParameterContext, ec: ExtensionContext): Any =
        when {
            pc.isHttpHandler() -> ClientFilters.SetBaseUriFrom(Uri.of("http://localhost:${control.port()}"))
                .then(OkHttp())
            else -> NoOp
        }
}
