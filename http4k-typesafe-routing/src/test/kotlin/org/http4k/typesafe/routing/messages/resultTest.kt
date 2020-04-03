package org.http4k.typesafe.routing.messages

import com.natpryce.Failure
import com.natpryce.Success
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.routing.Simple.response
import org.http4k.typesafe.routing.Simple.result
import org.http4k.typesafe.routing.SimpleResponseRouting.status
import org.http4k.typesafe.routing.fix
import org.http4k.typesafe.routing.messages.body.textPlain
import org.junit.jupiter.api.Test

@Suppress("MemberVisibilityCanBePrivate")
internal class ResultTest {

    val lens = result(
        status(OK, response.text()),
        status(BAD_REQUEST, response.text()).fix())

    @Test
    fun `works for failure`() {
        responseContract(
            lens,
            Failure("something went wrong"),
            Response(BAD_REQUEST).textPlain("something went wrong"))
    }

    @Test
    fun `works for success`() {
        responseContract(
            lens,
            Success("hello world"),
            Response(OK).textPlain("hello world"))
    }
}