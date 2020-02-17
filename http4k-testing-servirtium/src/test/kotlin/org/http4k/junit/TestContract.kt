package org.http4k.junit

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.servirtium.InteractionControl
import org.junit.jupiter.api.Test

interface TestContract {

    @Test
    @JvmDefault
    fun scenario(handler: HttpHandler, control: InteractionControl) {
        control.addNote("this is a note")

        assertThat(handler(Request(POST, "/foobar").body("welcome")).bodyString(), equalTo("hello"))

        control.addNote("this is another note")

        assertThat(handler(Request(POST, "/foobar").body("welcome")).bodyString(), equalTo("hello"))

        control.addNote("this is yet another note")
    }
}
