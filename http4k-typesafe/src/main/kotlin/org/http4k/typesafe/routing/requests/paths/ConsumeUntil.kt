package org.http4k.typesafe.routing.requests.paths

import java.net.URLDecoder
import java.net.URLEncoder

typealias IndexInString = (String) -> Int

fun nextSlash(path: String) =
    path.indexOf('/').let { i ->
        if (i < 0) path.length else i
    }

class ConsumeUntil(val name: String, val index: IndexInString) : SimplePath<String> {
    override fun get(from: String): PathResult<String> {
        val leadingSlashes = leading.find(from)?.value ?: ""
        val noLeadingSlashes = from.substring(leadingSlashes.length)
        val i = this.index(noLeadingSlashes)

        return when {
            i < 0 ->
                matchFailure("Remaining path did not match")

            // TODO: can we improve on this message?
            i > noLeadingSlashes.length ->
                matchFailure("Cannot consume $i characters from ${noLeadingSlashes.length}-character path")

            else -> matchSuccess(
                URLDecoder.decode(noLeadingSlashes.substring(0, i), "utf8"),
                noLeadingSlashes.substring(i))
        }
    }

    override fun set(into: String, value: String): String =
        into / URLEncoder.encode(value, "utf8")
}