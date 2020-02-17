package org.http4k.junit

import org.http4k.servirtium.ServirtiumServer
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

interface HasServirtiumServer : BeforeEachCallback, AfterEachCallback {
    val control: ServirtiumServer

    override fun beforeEach(ec: ExtensionContext) {
        control.start()
    }

    override fun afterEach(ec: ExtensionContext) {
        control.stop()
    }
}
