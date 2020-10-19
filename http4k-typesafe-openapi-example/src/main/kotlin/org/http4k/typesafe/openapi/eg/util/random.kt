package org.http4k.typesafe.openapi.eg.util

import io.jsonwebtoken.lang.Strings.capitalize
import java.util.*
import kotlin.reflect.KFunction1

val adjectives = listOf(
    "brave", "intrepid", "cowardly", "gigantic", "massive", "infinite", "unfettered", "gruntled", "disgruntled",
    "cramped", "lavish", "miserly", "idiotic", "ill-advised", "complicated", "unexpected", "surprise")

val nouns = listOf(
    "batfink", "colonoscopy", "muppet", "gary", "flan", "giblets", "pontypandy", "potato", "penguin", "koala",
    "battery", "horse", "cake",  "marmot", "anaglypta", "cheeseboard", "handwaving", "flapping", "wrestling")

fun random(base: String): String {
    return "$base ${capitalize(random(adjectives))} ${capitalize(random(nouns))}"
}

val random = Random()
fun <T> random(ts: List<T>): T {
    return ts.get(random.nextInt(ts.size))
}
/**
 * Usage:
 *
 * ::MyUUIDIdentifier.random()
 */
fun <R> KFunction1<UUID, R>.random(): R {
    return this(UUID.randomUUID())
}
