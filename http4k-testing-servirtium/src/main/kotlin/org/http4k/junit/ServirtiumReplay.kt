package org.http4k.junit

import org.http4k.core.then
import org.http4k.servirtium.InteractionControl.Companion.NoOp
import org.http4k.servirtium.InteractionOptions
import org.http4k.servirtium.StorageProvider
import org.http4k.traffic.Replay
import org.http4k.traffic.Servirtium
import org.http4k.traffic.replayingMatchingContent
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

/**
 * JUnit 5 extension for replaying HTTP traffic from Servirtium format.
 */
class ServirtiumReplay(private val baseName: String,
                       private val storageProvider: StorageProvider,
                       private val options: InteractionOptions = InteractionOptions.Companion.Defaults) : ParameterResolver {
    override fun supportsParameter(pc: ParameterContext, ec: ExtensionContext) = pc.isHttpHandler() || pc.isInteractionControl()

    override fun resolveParameter(pc: ParameterContext, ec: ExtensionContext): Any =
        when {
            pc.isHttpHandler() -> ConvertBadResponseToAssertionFailed()
                .then(Replay.Servirtium(storageProvider("$baseName.${ec.requiredTestMethod.name}"), options)
                    .replayingMatchingContent(options::modify)
                )
            else -> NoOp
        }
}
