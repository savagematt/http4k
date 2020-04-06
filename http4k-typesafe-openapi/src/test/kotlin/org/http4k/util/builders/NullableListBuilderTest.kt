package org.http4k.util.builders

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.typesafe.openapi.builders.BaseBuilder
import org.junit.jupiter.api.Test

internal class NullableListBuilderTest {
    class PretendBuilder(original: String) :
        BaseBuilder<String, PretendBuilder>(::PretendBuilder) {
        var value = original
        override fun invoke(f: PretendBuilder.() -> Unit) = f(this)

        override fun build() = value
    }


    @Test
    fun `remains null`() {
        val list = NullableListBuilder(null, ::PretendBuilder)
        assertThat(
            list.build(),
            equalTo<List<String>?>(null)
        )
    }

    @Test
    fun `can add item to null`() {
        val list = NullableListBuilder(null, ::PretendBuilder)
        list += "a"
        assertThat(
            list.build(),
            equalTo<List<String>?>(listOf("a"))
        )
    }

    @Test
    fun `can add item to existing`() {
        val list = NullableListBuilder(listOf("a"), ::PretendBuilder)
        list += "b"
        assertThat(
            list.build(),
            equalTo<List<String>?>(listOf("a", "b"))
        )
    }

    @Test
    fun `can map items using builder dsl`() {
        val list = NullableListBuilder(listOf("a", "b"), ::PretendBuilder)
        list.map { value += "-modified" }
        assertThat(
            list.build(),
            equalTo<List<String>?>(listOf("a-modified", "b-modified"))
        )
    }

    @Test
    fun `mapping items for null does nothing`() {
        val list = NullableListBuilder(null, ::PretendBuilder)
        list.map { value += "-modified" }
        assertThat(
            list.build(),
            equalTo<List<String>?>(null)
        )
    }
}