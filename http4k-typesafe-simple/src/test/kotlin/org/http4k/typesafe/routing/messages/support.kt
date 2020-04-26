package org.http4k.typesafe.routing.messages

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.recover
import org.http4k.core.HttpMessage
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RequestLens
import org.http4k.typesafe.routing.ResponseLens

private fun <M : HttpMessage, T> lensContract(
    lens: MessageLens<M, T>,
    setValue: T,
    injectIntoMessage: M,
    expectedMessage: M,
    expectedGetValue: T) {

    val generatedMessage = lens.set(
        injectIntoMessage,
        setValue)
        .recover { injectIntoMessage }

    assertThat(generatedMessage, equalTo(expectedMessage))

    assertThat(
        lens.get(generatedMessage).recover { throw AssertionError("Lens failed: ${it.message}") },
        equalTo(expectedGetValue))
}

fun <T> requestContract(lens: RequestLens<T>,
                        setValue: T,
                        injectIntoMessage: Request = Request(GET, "/"),
                        expectedRequest: Request,
                        expectedGetValue: T = setValue) {
    lensContract(lens, setValue, injectIntoMessage, expectedRequest, expectedGetValue)
}

fun <T> responseContract(lens: ResponseLens<T>,
                         expectedValue: T,
                         injectIntoMessage: Response = Response(OK),
                         expectedResponse: Response,
                         expectedGetValue: T = expectedValue) {
    lensContract(lens, expectedValue, injectIntoMessage, expectedResponse, expectedGetValue)
}