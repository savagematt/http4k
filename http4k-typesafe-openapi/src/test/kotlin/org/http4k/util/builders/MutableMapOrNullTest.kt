package org.http4k.util.builders

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class MutableMapOrNullTest {
    @Test
    fun `remains null`() {
        val map = MutableMapOrNull<String, String>(null)
        assertThat(
            map.toMap(),
            equalTo<Map<String, String>?>(null)
        )
    }

    @Test
    fun `can add item to null`() {
        val map = MutableMapOrNull<String, String>(null)
        map += "a" to "123"
        assertThat(
            map.toMap(),
            equalTo<Map<String, String>?>(mapOf("a" to "123"))
        )
    }

    @Test
    fun `can add item to existing`() {
        val map = MutableMapOrNull<String, String>(mapOf("a" to "123"))
        map += "b" to "456"
        assertThat(
            map.toMap(),
            equalTo<Map<String, String>?>(mapOf("a" to "123", "b" to "456"))
        )
    }
}