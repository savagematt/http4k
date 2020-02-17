package org.http4k.junit

import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * JUnit 5 extension for recording HTTP traffic to disk in Servirtium format.
 */
interface TracksTestState : BeforeTestExecutionCallback, AfterTestExecutionCallback {
    val inTest: Boolean

    companion object {
        operator fun invoke() = object : TracksTestState {
            override var inTest = false

            override fun beforeTestExecution(ec: ExtensionContext?) {
                inTest = true
            }

            override fun afterTestExecution(ec: ExtensionContext) {
                inTest = false
            }
        }
    }
}
