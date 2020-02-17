package org.http4k.junit

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.throws
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.servirtium.InteractionOptions
import org.http4k.servirtium.InteractionStorage.Companion.InMemory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.opentest4j.AssertionFailedError
import java.io.File

class ServirtiumReplayIntegrationTest : TestContract {

    private val storage = InMemory()

    init {
        File("src/test/resources/org/http4k/junit/ServirtiumReplayIntegrationTest.check contents are recorded as per manipulations.approved").also {
            storage("contractName.scenario").accept(it.readText().toByteArray())
            storage("contractName.unexpected content").accept(it.readText().toByteArray())
            storage("contractName.too many requests").accept(it.readText().toByteArray())
        }
    }

    @JvmField
    @RegisterExtension
    val replay = ServirtiumReplay("contractName", storage,
        object : InteractionOptions {
            override fun modify(request: Request) = request.body(request.bodyString().replace("2", ""))
        })

    @Test
    fun `unexpected content`(handler: HttpHandler) {
        assertThat({
            handler(Request(Method.GET, "/foobar").body("welcome"))
        }, throws(
            has(AssertionFailedError::getLocalizedMessage, containsSubstring("Unexpected request received for Interaction 0"))))
    }

    @Test
    fun `too many requests`(handler: HttpHandler) {
        handler(Request(Method.POST, "/foobar").body("welcome"))
        handler(Request(Method.POST, "/foobar").body("welcome"))
        assertThat({
            handler(Request(Method.POST, "/foobar").body("welcome"))
        }, throws(
            has(AssertionFailedError::getLocalizedMessage, containsSubstring("Unexpected request received for Interaction 2"))))
    }
}
