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

val emptyRequest = Request(GET, "/")


@Suppress("UNCHECKED_CAST")
fun <M : HttpMessage, T> lensContract(lens: MessageLens<M, T>, expectedValue: T, expectedMessage: M) {
    val emptyMessage = when (expectedMessage) {
        is Request -> Request(GET, "/")
        else -> Response(OK)
    } as M

    val generatedMessage = lens.set(
        emptyMessage,
        expectedValue)
        .recover { emptyMessage }

    assertThat(generatedMessage, equalTo(expectedMessage))

    assertThat(
        lens.get(generatedMessage).recover { throw AssertionError("Lens failed") },
        equalTo(expectedValue))
}

fun <T> requestContract(lens: RequestLens<T>,
                        expectedValue: T,
                        expectedRequest: Request) {
    lensContract(lens, expectedValue, expectedRequest)
}

fun <T> responseContract(lens: ResponseLens<T>,
                         expectedValue: T,
                         expectedResponse: Response) {
    lensContract(lens, expectedValue, expectedResponse)
}

fun <T> lensContract(lens: MessageLens<HttpMessage, T>,
                     expectedValue: T,
                     expectedRequest: Request,
                     expectedResponse: Response) {

    lensContract(lens, expectedValue, expectedRequest)
    lensContract(lens, expectedValue, expectedResponse)
}
