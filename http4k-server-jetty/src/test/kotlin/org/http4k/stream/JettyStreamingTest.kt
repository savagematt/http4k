package org.http4k.stream

import org.http4k.client.ApacheClient
import org.http4k.core.BodyMode.Stream
import org.http4k.core.HttpHandler
import org.http4k.server.Jetty
import org.http4k.server.ServerConfig
import org.http4k.streaming.StreamingContract
import org.junit.jupiter.api.Disabled

class JettyStreamingTest : StreamingContract() {
    override fun serverConfig(): ServerConfig = Jetty(0)

    override fun createClient(): HttpHandler =
            ApacheClient(requestBodyMode = Stream, responseBodyMode = Stream)

    @Disabled("New ApacheClient is missing streaming support")
    override fun `can stream response`() {
    }
}