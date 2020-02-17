package org.http4k.junit

import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * JUnit 5 extension for recording HTTP traffic to disk in Servirtium format.
 */
interface TrackTestState : BeforeTestExecutionCallback, AfterTestExecutionCallback {
    val inTest: Boolean

    companion object {
        operator fun invoke() = object : TrackTestState {
            override var inTest = false

            override fun beforeTestExecution(context: ExtensionContext?) {
                inTest = true
            }

            override fun afterTestExecution(context: ExtensionContext) {
                inTest = false
            }
        }
    }
}
