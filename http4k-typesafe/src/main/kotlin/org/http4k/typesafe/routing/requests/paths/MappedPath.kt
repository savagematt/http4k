package org.http4k.typesafe.routing.requests.paths

import com.natpryce.flatMap
import org.http4k.lens.BiDiMapping
import org.http4k.lens.StringBiDiMappings
import java.time.format.DateTimeFormatter

/**
 * Catches Exception from the mapping function and returns
 * failureMessage as a `Failure(NoMatch(reason))`
 */
fun <A, B> mapper(f: (A) -> B, failureMessage: String) = { match: Match<A> ->
    try {
        matchSuccess(f(match.value), match.remaining)
    } catch (e: Exception) {
        matchFailure(failureMessage)
    }
}

open class MappedPath<A, B>(
    val initial: Path<A>,
    val getter: (Match<A>) -> PathResult<B>,
    val setter: (B) -> A
) : SimplePath<B> {

    override fun get(from: String): PathResult<B> =
        initial.get(from).flatMap {
            getter(it)
        }

    override fun set(into: String, value: B): String =
        initial.set(into, setter(value))

    override fun toString() = initial.toString()
}

fun <A, B> Path<A>.map(mapping: BiDiMapping<A, B>, failureMessage: String) =
    MappedPath(
        this,
        mapper(mapping.asOut, failureMessage),
        mapping.asIn)

object Mapped{

    fun nonEmptyString(path: Path<String>) =
        path.map(
            StringBiDiMappings.nonEmpty(), "Expected a non-empty string")

    fun int(path: Path<String>) =
        path.map(
            StringBiDiMappings.int(), "Expected an integer")

    fun float(path: Path<String>) =
        path.map(
            StringBiDiMappings.float(), "Expected a float")

    fun double(path: Path<String>) =
        path.map(
            StringBiDiMappings.double(), "Expected a double")

    fun long(path: Path<String>) =
        path.map(
            StringBiDiMappings.long(), "Expected a long")

    fun bigDecimal(path: Path<String>) =
        path.map(
            StringBiDiMappings.bigDecimal(), "Expected a number")

    fun bigInteger(path: Path<String>) =
        path.map(
            StringBiDiMappings.bigInteger(), "Expected an integer")

    fun boolean(path: Path<String>) =
        path.map(
            StringBiDiMappings.boolean(), "Expected true or false")

    fun base64(path: Path<String>) =
        path.map(
            StringBiDiMappings.base64(), "Expected a base-64 encoded string")

    fun uuid(path: Path<String>) =
        path.map(
            StringBiDiMappings.uuid(), "Expected a uuid")

    fun uri(path: Path<String>) =
        path.map(
            StringBiDiMappings.uri(), "Expected a uri")

    fun regex(path: Path<String>, pattern: String, group: Int) =
        path.map(
            StringBiDiMappings.regex(pattern, group), "Did not match regex: $pattern")

    fun regexObject(path: Path<String>) =
        path.map(
            StringBiDiMappings.regexObject(), "Expected a regex")

    fun duration(path: Path<String>) =
        path.map(
            StringBiDiMappings.duration(), "Expected a duration")

    fun yearMonth(path: Path<String>) =
        path.map(
            StringBiDiMappings.yearMonth(), "Expected a year month in format yyyy-MM")

    fun instant(path: Path<String>) =
        path.map(
            StringBiDiMappings.instant(), "Expected a moment in time in format ${DateTimeFormatter.ISO_INSTANT}")

    fun dateTime(path: Path<String>, formatter: DateTimeFormatter) =
        path.map(
            StringBiDiMappings.localDateTime(formatter),
            "Expected date time in format $formatter")

    fun zonedDateTime(path: Path<String>, formatter: DateTimeFormatter) =
        path.map(
            StringBiDiMappings.zonedDateTime(formatter),
            "Expected date time in format $formatter")

    fun localDate(path: Path<String>, formatter: DateTimeFormatter) =
        path.map(
            StringBiDiMappings.localDate(formatter),
            "Expected date in format $formatter")

    fun localTime(path: Path<String>, formatter: DateTimeFormatter) =
        path.map(
            StringBiDiMappings.localTime(formatter),
            "Expected date in format $formatter")
}