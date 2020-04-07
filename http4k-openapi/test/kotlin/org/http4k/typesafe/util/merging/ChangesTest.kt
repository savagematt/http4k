package org.http4k.util.merging

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class ChangesTest {
    data class Cheese(val density: Int,
                      val cheapness: String,
                      val smell: String,
                      val rind: String)

    @Test
    fun `use latest() to get values of fields that have changed from the reference value`() {
        Cheese(0, "reference", "reference", "reference").changes(
            Cheese(1, "a", "reference", "reference"),
            Cheese(2, "reference", "b", "reference")) {

            assertThat(
                latest { it.density },
                equalTo(2))

            assertThat(
                latest { it.cheapness },
                equalTo("a"))

            assertThat(
                latest { it.smell },
                equalTo("b"))

            assertThat(
                latest { it.rind },
                equalTo("reference"))
        }
    }

    @Test
    fun `latest() will return the field value from the first version where it changed from the reference`() {
        val reference = Cheese(0, "reference", "reference", "reference")
        val b = reference.copy(cheapness = "b")
        val a = reference.copy(density = 2)

        val cheese = reference.changes(b, a) {
            Cheese(
                latest { it.density },
                latest { it.cheapness },
                latest { it.smell },
                latest { it.rind }
            )
        }

        assertThat(cheese, equalTo(
            Cheese(2, "b", "reference", "reference")))
    }

    data class Sheep(val cheese: Cheese?)

    @Test
    fun `works with nulls in reference and a`() {
        val reference = Sheep(null)
        val b = Sheep(Cheese(1, "b", "b", "b"))
        val a = Sheep(null)

        val sheep: Sheep = reference.changes(b, a) {
            Sheep(latest { it.cheese })
        }

        assertThat(sheep, equalTo(b))
    }

    @Test
    fun `works with nulls in reference and b`() {
        val reference = Sheep(null)
        val b = Sheep(null)
        val a = Sheep(Cheese(2, "a", "a", "a"))

        val sheep: Sheep = reference.changes(b, a) {
            Sheep(latest { it.cheese })
        }

        assertThat(sheep, equalTo(a))
    }

    @Test
    fun `works with all nulls`() {
        val reference = Sheep(null)
        val b = Sheep(null)
        val a = Sheep(null)

        val sheep: Sheep = reference.changes(a, b) {
            Sheep(
                latest { it.cheese }
            )
        }

        assertThat(sheep, equalTo(Sheep(null)))
    }

    @Test
    fun `drill into null in reference, nulls in both`() {
        val sheep: Sheep = sheep(
            Sheep(null),
            Sheep(null),
            Sheep(null),
            Cheese(0, "null", "null", "null"))

        assertThat(sheep, equalTo(
            Sheep(null)))
    }

    @Test
    fun `drill into null in reference, values in both`() {
        val nullCheese = Cheese(0, "null", "null", "null")

        val sheep: Sheep = sheep(
            Sheep(null),
            Sheep(nullCheese.copy(cheapness = "a")),
            Sheep(nullCheese.copy(density = 2)),
            nullCheese)

        assertThat(sheep, equalTo(
            Sheep(Cheese(2, "a", "null", "null"))))
    }

    @Test
    fun `drill down null in reference, value in a`() {
        val nullCheese = Cheese(0, "null", "null", "null")

        val sheep: Sheep = sheep(
            Sheep(null),
            Sheep(nullCheese.copy(cheapness = "a")),
            Sheep(null),
            nullCheese)

        assertThat(sheep, equalTo(
            Sheep(Cheese(0, "a", "null", "null"))))
    }

    @Test
    fun `drill down null in reference, value in b`() {
        val nullCheese = Cheese(0, "null", "null", "null")

        val sheep: Sheep = sheep(
            Sheep(null),
            Sheep(null),
            Sheep(nullCheese.copy(cheapness = "b")),
            nullCheese)

        assertThat(sheep, equalTo(
            Sheep(Cheese(0, "b", "null", "null"))))
    }

    @Test
    fun `drill into value in reference, nulls in both`() {
        val sheep: Sheep = sheep(
            Sheep(Cheese(999, "reference", "reference", "reference")),
            Sheep(null),
            Sheep(null),
            Cheese(0, "null", "null", "null"))

        assertThat(sheep, equalTo(
            Sheep(null)))
    }

    @Test
    fun `drill into value in reference, values in both`() {
        val referenceCheese = Cheese(999, "reference", "reference", "reference")

        val sheep: Sheep = sheep(
            Sheep(referenceCheese),
            Sheep(referenceCheese.copy(cheapness = "a")),
            Sheep(referenceCheese.copy(density = 2)),
            Cheese(0, "null", "null", "null"))

        assertThat(sheep, equalTo(
            Sheep(Cheese(2, "a", "reference", "reference"))))
    }

    @Test
    fun `drill down value in reference, value in a`() {
        val referenceCheese = Cheese(999, "reference", "reference", "reference")

        val sheep: Sheep = sheep(
            Sheep(referenceCheese),
            Sheep(referenceCheese.copy(cheapness = "a")),
            Sheep(null),
            Cheese(0, "null", "null", "null"))

        assertThat(sheep, equalTo(
            Sheep(Cheese(999, "a", "reference", "reference"))))
    }

    @Test
    fun `drill down value in reference, value in b, prioritises a`() {
        val referenceCheese = Cheese(999, "reference", "reference", "reference")
        val b = Sheep(referenceCheese.copy(cheapness = "b"))

        val sheep: Sheep = sheep(
            Sheep(referenceCheese),
            Sheep(null),
            b,
            Cheese(0, "null", "null", "null"))

        assertThat(sheep, equalTo(
            b))
    }

    data class Horse(val cheese: Cheese)

    @Test
    fun `do not need to provide reference value for non-nullable fields`() {
        val reference = Horse(Cheese(999, "reference", "reference", "reference"))
        val b = Horse(Cheese(2, "b", "reference", "reference"))
        val a = Horse(Cheese(1, "reference", "a", "reference"))

        val horse: Horse = reference.changes(b, a) {
            Horse(
                latest({ it.cheese }) {
                    Cheese(
                        latest { it.density },
                        latest { it.cheapness },
                        latest { it.smell },
                        latest { it.rind }
                    )
                }
            )
        }

        assertThat(horse, equalTo(
            Horse(Cheese(1, "b", "a", "reference"))))
    }
}

private fun sheep(reference: ChangesTest.Sheep, a: ChangesTest.Sheep, b: ChangesTest.Sheep, nullValue: ChangesTest.Cheese): ChangesTest.Sheep {
    return reference.changes(a, b) {
        ChangesTest.Sheep(
            latest({ it.cheese }, nullValue) {
                ChangesTest.Cheese(
                    latest { it.density },
                    latest { it.cheapness },
                    latest { it.smell },
                    latest { it.rind }
                )
            }
        )
    }
}
