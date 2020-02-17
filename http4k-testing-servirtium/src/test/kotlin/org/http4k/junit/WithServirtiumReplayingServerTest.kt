package org.http4k.junit

import org.http4k.core.Request
import org.http4k.servirtium.InteractionOptions
import org.http4k.servirtium.InteractionStorage.Companion.Disk
import org.http4k.testing.ApprovalTest
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File

@ExtendWith(ApprovalTest::class)
class WithServirtiumReplayingServerTest : TestContract {
    private val storage = Disk(File("."))

    init {
        File("src/test/resources/org/http4k/junit/ServirtiumReplayIntegrationTest.check contents are recorded as per manipulations.approved").also {
            storage("contractName.scenario").accept(it.readText().toByteArray())
            storage("contractName.unexpected content").accept(it.readText().toByteArray())
            storage("contractName.too many requests").accept(it.readText().toByteArray())
        }
    }

    @RegisterExtension
    @JvmField
    val control = WithServirtiumReplayServer("contractName", storage,
        object : InteractionOptions {
            override fun modify(request: Request) = request.body(request.bodyString().replace("2", ""))
        })
}
