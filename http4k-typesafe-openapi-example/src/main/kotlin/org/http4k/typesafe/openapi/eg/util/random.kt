package org.http4k.typesafe.openapi.eg.util

import io.jsonwebtoken.lang.Strings.capitalize
import java.util.*
import kotlin.reflect.KFunction1

val adjectives = listOf(
    "intrepid", "uncertain", "gigantic", "massive", "infinite", "unfettered", "gruntled", "disgruntled", "startled",
    "cramped", "lavish", "miserly", "idiotic", "ill-advised", "complicated", "unexpected", "surprise", "confused",
    "confusing", "excitable", "floating", "unfamiliar", "unpopular", "popular", "incompetent", "competent",
    "reassuring", "over-ambitious", "tentative", "temporary", "unnecessary", "reluctant", "uncomfortable",
    "comfortable", "precise")

val nouns = listOf(
    "batfink", "colonoscopy", "muppet", "gary", "dave", "flan", "giblets", "chitterlings", "pontypandy", "potato",
    "penguin", "koala", "battery", "horse", "cake", "marmot", "cheeseboard", "handwaving", "flapping", "wrestling",
    "smooching", "bingo-wings", "northerner", "Londoner", "hipster", "granny", "kittens", "mittens", "meme")

fun random(base: String): String =
    "${capitalize(random(adjectives))} ${capitalize(random(nouns))} ${capitalize(base)}"

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
