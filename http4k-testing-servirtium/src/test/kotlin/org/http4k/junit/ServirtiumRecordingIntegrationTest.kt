package org.http4k.junit

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.servirtium.InteractionControl
import org.http4k.servirtium.InteractionOptions
import org.http4k.servirtium.InteractionStorage.Companion.InMemory
import org.http4k.testing.ApprovalTest
import org.http4k.testing.Approver
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(ApprovalTest::class)
class ServirtiumRecordingIntegrationTest : TestContract {

    private val storage = InMemory()

    private val originalHandler: HttpHandler = { Response(Status.OK).body("hello") }

    @JvmField
    @RegisterExtension
    val record = ServirtiumRecording(
        "contractName",
        originalHandler,
        storage,
        object : InteractionOptions {
            override fun modify(request: Request) = request.body(request.bodyString() + request.bodyString())
            override fun modify(response: Response) = response.body(response.bodyString() + "2")
        }
    )

    @Test
    fun `check contents are recorded as per manipulations`(
        handler: HttpHandler,
        control: InteractionControl,
        approver: Approver
    ) {
        super.scenario(handler, control)
        approver.assertApproved(Response(Status.OK).body(
            String(storage("contractName.check contents are recorded as per manipulations").get())
        ))
    }

    @AfterEach
    fun `check that raw handler is passed into non test method`(handler: HttpHandler) {
        Assertions.assertTrue(originalHandler === handler)
    }
}
