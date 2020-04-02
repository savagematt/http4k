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

fun <M : HttpMessage, T> lensContract(
    lens: MessageLens<M, T>,
    injectIntoMessage: M,
    expectedValue: T,
    expectedMessage: M) {

    val generatedMessage = lens.set(
        injectIntoMessage,
        expectedValue)
        .recover { injectIntoMessage }

    assertThat(generatedMessage, equalTo(expectedMessage))

    assertThat(
        lens.get(generatedMessage).recover { throw AssertionError("Lens failed") },
        equalTo(expectedValue))
}

fun <T> requestContract(lens: RequestLens<T>,
                        expectedValue: T,
                        expectedRequest: Request,
                        injectIntoMessage: Request = Request(GET, "/")) {
    lensContract(lens, injectIntoMessage, expectedValue, expectedRequest)
}

fun <T> responseContract(lens: ResponseLens<T>,
                         expectedValue: T,
                         expectedResponse: Response,
                         injectIntoMessage: Response = Response(OK)) {
    lensContract(lens, injectIntoMessage, expectedValue, expectedResponse)
}

fun <T> lensContract(lens: MessageLens<HttpMessage, T>,
                     expectedValue: T,
                     expectedRequest: Request,
                     expectedResponse: Response,
                     injectIntoRequest: Request = Request(GET, "/"),
                     injectIntoResponse: Response = Response(OK)) {

    lensContract(lens, injectIntoRequest, expectedValue, expectedRequest)
    lensContract(lens, injectIntoResponse, expectedValue, expectedResponse)
}
