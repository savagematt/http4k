package org.http4k.typesafe.routing.requests.paths

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.typesafe.data.Tuple10
import org.http4k.typesafe.data.Tuple2
import org.http4k.typesafe.data.Tuple3
import org.http4k.typesafe.data.tuple
import org.http4k.typesafe.routing.SimplePaths.consume
import org.http4k.typesafe.routing.SimplePaths.div
import org.http4k.typesafe.routing.SimplePaths.get
import org.junit.jupiter.api.Test

class PathTest {
    @Test
    fun `can add path to string`() {
        val a = consume("a")

        val path = "widgets" / a

        assertThat(
            path.get("widgets/a-value"),
            equalTo<PathResult<String>>(
                matchSuccess(
                    "a-value", ""))
        )
    }

    @Test
    fun `can add string to path`() {
        val a = consume("a")

        val path = a / "widgets"

        assertThat(
            path.get("/a-value/widgets"),
            equalTo<PathResult<String>>(
                matchSuccess(
                    "a-value", ""))
        )
    }

    @Test
    fun `can add path to path`() {
        val a = consume("a")
        val b = consume("a")

        val path = a / b

        assertThat(
            path.get("/a-value/b-value"),
            equalTo<PathResult<Tuple2<String, String>>>(
                matchSuccess(
                    tuple("a-value", "b-value"), ""))
        )
    }

    @Test
    fun `can combine a mix of strings and paths`() {
        val a = consume("a")
        val b = consume("a")
        val c = consume("c")

        val path = "widgets" / a / "components" / b / c

        assertThat(
            path.get("widgets/a-value/components/b-value/c-value"),
            equalTo<PathResult<Tuple3<String, String, String>>>(
                matchSuccess(
                    tuple("a-value", "b-value", "c-value"), ""))
        )
    }

    @Test
    fun `can combine paths up to 10 segments`() {
        val a = consume("a")
        val b = consume("b")
        val c = consume("c")
        val d = consume("d")
        val e = consume("e")
        val f = consume("f")
        val g = consume("g")
        val h = consume("h")
        val i = consume("i")
        val j = consume("j")

        val path =
            a / b / c / d / e / f / g / h / i / j

        assertThat(
            path.get("a/b/c/d/e/f/g/h/i/j/remaining"),
            equalTo<PathResult<Tuple10<String, String, String, String, String, String, String, String, String, String>>>(
                matchSuccess(
                    tuple("a", "b", "c", "d", "e", "f", "g", "h", "i", "j"), "/remaining"))
        )
    }
}