package org.http4k.typesafe.openapi.requests.paths

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.typesafe.openapi.routing.pathVar
import org.http4k.typesafe.openapi.routing.div
import org.http4k.typesafe.routing.requests.paths.PathResult
import org.http4k.typesafe.routing.requests.paths.matchSuccess
import org.http4k.util.data.Tuple10
import org.http4k.util.data.Tuple2
import org.http4k.util.data.Tuple3
import org.http4k.util.data.tuple
import org.junit.jupiter.api.Test

class PathTest {
    @Test
    fun `can add path to string`() {
        val a = pathVar("a")

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
        val a = pathVar("a")

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
        val a = pathVar("a")
        val b = pathVar("a")

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
        val a = pathVar("a")
        val b = pathVar("a")
        val c = pathVar("c")

        val path = "widgets" / a / "components" / b / c

        val get = path.get("widgets/a-value/components/b-value/c-value")
        assertThat(
            get,
            equalTo<PathResult<Tuple3<String, String, String>>>(
                matchSuccess(
                    tuple("a-value", "b-value", "c-value"), ""))
        )
    }

    @Test
    fun `can combine paths up to 10 segments`() {
        val a = pathVar("a")
        val b = pathVar("b")
        val c = pathVar("c")
        val d = pathVar("d")
        val e = pathVar("e")
        val f = pathVar("f")
        val g = pathVar("g")
        val h = pathVar("h")
        val i = pathVar("i")
        val j = pathVar("j")

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