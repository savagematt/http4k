package org.http4k.junit

import org.http4k.servirtium.InteractionStorage.Companion.Disk
import org.http4k.testing.ApprovalTest
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File

@ExtendWith(ApprovalTest::class)
class WithServirtiumRecordingServerTest : TestContract {
    @RegisterExtension
    @JvmField
    val control = WithServirtiumRecordingServer("contractName", Disk(File(".")))
}

