package org.http4k.typesafe.openapi

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class AppendableOrNullTest {
    @Test
    fun `remains null`() {
        val list = AppendableOrNull<String>(null)
        assertThat(
            list.toList(),
            equalTo<List<String>?>(null)
        )
    }

    @Test
    fun `can add item to null`() {
        val list = AppendableOrNull<String>(null)
        list += "a"
        assertThat(
            list.toList(),
            equalTo<List<String>?>(listOf("a"))
        )
    }

    @Test
    fun `can add item to existing`() {
        val list = AppendableOrNull(listOf("a"))
        list += "b"
        assertThat(
            list.toList(),
            equalTo<List<String>?>(listOf("a", "b"))
        )
    }

}