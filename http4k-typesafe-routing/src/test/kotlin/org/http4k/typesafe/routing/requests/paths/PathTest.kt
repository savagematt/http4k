package org.http4k.typesafe.routing.requests.paths

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.typesafe.data.Tuple10
import org.http4k.typesafe.data.Tuple2
import org.http4k.typesafe.data.Tuple3
import org.http4k.typesafe.data.tuple
import org.junit.jupiter.api.Test

class PathTest {
    @Test
    fun `can add path to string`() {
        val a = ConsumeUntil.nextSlash("a")

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
        val a = ConsumeUntil.nextSlash("a")

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
        val a = ConsumeUntil.nextSlash("a")
        val b = ConsumeUntil.nextSlash("a")

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
        val a = ConsumeUntil.nextSlash("a")
        val b = ConsumeUntil.nextSlash("a")
        val c = ConsumeUntil.nextSlash("c")

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
        val a = ConsumeUntil.nextSlash("a")
        val b = ConsumeUntil.nextSlash("b")
        val c = ConsumeUntil.nextSlash("c")
        val d = ConsumeUntil.nextSlash("d")
        val e = ConsumeUntil.nextSlash("e")
        val f = ConsumeUntil.nextSlash("f")
        val g = ConsumeUntil.nextSlash("g")
        val h = ConsumeUntil.nextSlash("h")
        val i = ConsumeUntil.nextSlash("i")
        val j = ConsumeUntil.nextSlash("j")

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