package org.http4k.typesafe.routing.messages

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.util.data.tuple
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.and
import org.http4k.typesafe.routing.request
import org.junit.jupiter.api.Test

class TuplesTest {
    @Test
    fun `can combine up to 10 lenses`() {
        with(request) {
            val a = header("a")
            val b = header("b")
            val c = header("c")
            val d = header("d")
            val e = header("e")
            val f = header("f")
            val g = header("g")
            val h = header("h")
            val i = header("i")
            val j = header("j")

            val lens = a and b and c and d and e and f and g and h and i and j

            assertThat(
                lens.set(Request(GET, "/"),
                    tuple("a", "b", "c", "d", "e", "f", "g", "h", "i", "j")),

                equalTo<Result<Request, RoutingError>>(Success(Request(GET, "/")
                    .header("a", "a")
                    .header("b", "b")
                    .header("c", "c")
                    .header("d", "d")
                    .header("e", "e")
                    .header("f", "f")
                    .header("g", "g")
                    .header("h", "h")
                    .header("i", "i")
                    .header("j", "j")
                )))
        }

    }

}