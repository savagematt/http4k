package org.http4k.typesafe.routing.requests.paths

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.typesafe.routing.requests.paths.MatchResult.Companion.matchFailure
import org.http4k.typesafe.routing.requests.paths.MatchResult.Companion.matchSuccess
import org.junit.jupiter.api.Test

class ConsumeUntilTest {
    private fun untilIndex(count: Int) = ConsumeUntil("name") { count }

    @Test
    fun `consumes until index`() {
        assertThat(
            untilIndex(4).get("abcdef"),
            equalTo<PathResult<String>>(
                matchSuccess(
                    "abcd", "ef"))
        )
    }

    @Test
    fun `does not match if index is out of bounds`() {
        assertThat(
            untilIndex(4).get("abc"),
            equalTo<PathResult<String>>(matchFailure(
                "Cannot consume 4 characters from 3-character path", "abc"))
        )
    }

    @Test
    fun `does not match if index is -1`() {
        assertThat(
            untilIndex(-1).get("abc"),
            equalTo<PathResult<String>>(matchFailure(
                "Remaining path did not match", "abc"))
        )
    }

    @Test
    fun `strips leading slashes`() {
        assertThat(
            ConsumeUntil.nextSlash("name").get("////abc/def"),
            equalTo<PathResult<String>>(matchSuccess(
                "abc", "/def"))
        )
    }

    @Test
    fun `decodes url-encoded values`() {
        assertThat(
            untilIndex(3).get("a+bcde"),
            equalTo<PathResult<String>>(matchSuccess(
                "a b", "cde"))
        )
    }

    @Test
    fun `url-encodes values when setting`() {
        assertThat(
            untilIndex(3).set("existing/path/", "a b"),
            equalTo("existing/path/a+b")
        )
    }

}