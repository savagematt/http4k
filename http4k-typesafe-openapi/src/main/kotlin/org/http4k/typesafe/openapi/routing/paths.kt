package org.http4k.typesafe.openapi.routing

import com.natpryce.Success
import org.http4k.lens.BiDiMapping
import org.http4k.lens.StringBiDiMappings
import org.http4k.openapi.OpenApiParameter
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.openapi.ParameterLocation
import org.http4k.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.openapi.real
import org.http4k.typesafe.openapi.OpenApiPath
import org.http4k.typesafe.openapi.OpenApiPath2
import org.http4k.typesafe.routing.fold
import org.http4k.typesafe.routing.requests.paths.ConsumeUntil
import org.http4k.typesafe.routing.requests.paths.IgnoreFirst
import org.http4k.typesafe.routing.requests.paths.IgnoreSecond
import org.http4k.typesafe.routing.requests.paths.IndexInString
import org.http4k.typesafe.routing.requests.paths.Literal
import org.http4k.typesafe.routing.requests.paths.MappedPath
import org.http4k.typesafe.routing.requests.paths.Match
import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.joinPaths
import org.http4k.typesafe.routing.requests.paths.matchFailure
import org.http4k.typesafe.routing.requests.paths.matchSuccess
import org.http4k.typesafe.routing.requests.paths.nextSlash
import java.time.format.DateTimeFormatter
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3

operator fun <T> String.div(
    next: OpenApiPath<T>): OpenApiPath<T> {
    val literal = literal(this)
    return IgnoreFirst(literal, next) documentation fold(literal, next)
}

operator fun <T> OpenApiPath<T>.div(
    next: String) =
    this ignore literal(next)

infix fun <T> OpenApiPath<T>.ignore(next: OpenApiPath<Unit>) =
    IgnoreSecond(this, next) documentation fold(this, next)

infix operator fun <A, B> OpenApiPath<A>.div(next: OpenApiPath<B>) =
    OpenApiPath2(this, next)

fun pathVar(
    name: String, index: IndexInString = ::nextSlash) =
    ConsumeUntil(name, index) openapi pathParam(name)

fun literal(value: String) =
    Literal(value) openapi operationPath(value)

object Mapped {
    fun nonEmptyString(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.nonEmpty(), "Expected a non-empty string")

    fun int(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.int(), "Expected an integer")

    fun float(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.float(), "Expected a float")

    fun double(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.double(), "Expected a double")

    fun long(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.long(), "Expected a long")

    fun bigDecimal(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.bigDecimal(), "Expected a number")

    fun bigInteger(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.bigInteger(), "Expected an integer")

    fun boolean(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.boolean(), "Expected true or false")

    fun base64(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.base64(), "Expected a base-64 encoded string")

    fun uuid(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.uuid(), "Expected a uuid")

    fun uri(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.uri(), "Expected a uri")

    fun regex(path: OpenApiPath<String>, pattern: String, group: Int) =
        path.map(
            StringBiDiMappings.regex(pattern, group), "Did not match regex: $pattern")

    fun regexObject(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.regexObject(), "Expected a regex")

    fun duration(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.duration(), "Expected a duration")

    fun yearMonth(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.yearMonth(), "Expected a year month in format yyyy-MM")

    fun instant(path: OpenApiPath<String>) =
        path.map(
            StringBiDiMappings.instant(), "Expected a moment in time in format ${DateTimeFormatter.ISO_INSTANT}")

    fun dateTime(path: OpenApiPath<String>, formatter: DateTimeFormatter) =
        path.map(
            StringBiDiMappings.localDateTime(formatter),
            "Expected date time in format $formatter")

    fun zonedDateTime(path: OpenApiPath<String>, formatter: DateTimeFormatter) =
        path.map(
            StringBiDiMappings.zonedDateTime(formatter),
            "Expected date time in format $formatter")

    fun localDate(path: OpenApiPath<String>, formatter: DateTimeFormatter) =
        path.map(
            StringBiDiMappings.localDate(formatter),
            "Expected date in format $formatter")

    fun localTime(path: OpenApiPath<String>, formatter: DateTimeFormatter) =
        path.map(
            StringBiDiMappings.localTime(formatter),
            "Expected date in format $formatter")
}

fun OpenApiPath<String>.nonEmptyString() =
    wrap(Mapped::nonEmptyString, this)

fun OpenApiPath<String>.int() =
    wrap(Mapped::int, this)

fun OpenApiPath<String>.float() =
    wrap(Mapped::float, this)

fun OpenApiPath<String>.double() =
    wrap(Mapped::double, this)

fun OpenApiPath<String>.long() =
    wrap(Mapped::long, this)

fun OpenApiPath<String>.bigDecimal() =
    wrap(Mapped::bigDecimal, this)

fun OpenApiPath<String>.bigInteger() =
    wrap(Mapped::bigInteger, this)

fun OpenApiPath<String>.boolean() =
    wrap(Mapped::boolean, this)

fun OpenApiPath<String>.base64() =
    wrap(Mapped::base64, this)

fun OpenApiPath<String>.uuid() =
    wrap(Mapped::uuid, this)

fun OpenApiPath<String>.uri() =
    wrap(Mapped::uri, this)

fun OpenApiPath<String>.regex(pattern: String, group: Int) =
    wrap(Mapped::regex, this, pattern, group)

fun OpenApiPath<String>.regexObject() =
    wrap(Mapped::regexObject, this)

fun OpenApiPath<String>.duration() =
    wrap(Mapped::duration, this)

fun OpenApiPath<String>.yearMonth() =
    wrap(Mapped::yearMonth, this)

fun OpenApiPath<String>.instant() =
    wrap(Mapped::instant, this)

fun OpenApiPath<String>.dateTime(formatter: DateTimeFormatter) =
    wrap(Mapped::dateTime, this, formatter)

fun OpenApiPath<String>.zonedDateTime(formatter: DateTimeFormatter) =
    wrap(Mapped::zonedDateTime, this, formatter)

fun OpenApiPath<String>.localDate(formatter: DateTimeFormatter) =
    wrap(Mapped::localDate, this, formatter)

fun OpenApiPath<String>.localTime(formatter: DateTimeFormatter) =
    wrap(Mapped::localTime, this, formatter)

infix fun <T> Path<T, *>.openapi(
    docs: (OpenApiRouteInfoDsl.() -> Unit)) =
    this documentation { OpenApiRouteInfoDsl(it).also(docs).build() }

infix fun <T> Path<T, *>.documentation(
    docs: (OpenApiRouteDocs) -> OpenApiRouteDocs) =
    OpenApiPath(this, docs)

fun operationPath(value: String): OpenApiRouteInfoDsl.() -> Unit = {
    route {
        path = joinPaths(path, value)
    }
}

private fun pathParam(name: String): OpenApiRouteInfoDsl.() -> Unit = {
    route {
        path = joinPaths(path, "{$name}")
        operation {
            parameters += OpenApiParameter(ParameterLocation.PATH, name).real()
        }
    }
}

/**
 * Uses wrapper to wrap path in a MappedPath,
 * but preserves the behaviour of path.document()
 */
private fun <T> wrap(wrapper: KFunction1<OpenApiPath<String>, OpenApiPath<T>>,
                     path: OpenApiPath<String>): OpenApiPath<T> =
    wrapper(path) documentation fold(path)

/**
 * Uses wrapper to wrap path in a MappedPath,
 * but preserves the behaviour of path.document()
 */
private fun <T, A, B> wrap(wrapper: KFunction3<OpenApiPath<String>, A, B, OpenApiPath<T>>,
                           path: OpenApiPath<String>,
                           a: A,
                           b: B): OpenApiPath<T> =
    wrapper(path, a, b) documentation fold(path)

/**
 * Uses wrapper to wrap path in a MappedPath,
 * but preserves the behaviour of path.document()
 */
private fun <T, A> wrap(wrapper: KFunction2<OpenApiPath<String>, A, OpenApiPath<T>>,
                        path: OpenApiPath<String>,
                        a: A): OpenApiPath<T> =
    wrapper(path, a) documentation fold(path)


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

fun <A, B> OpenApiPath<A>.map(mapping: BiDiMapping<A, B>, failureMessage: String): OpenApiPath<B> =
    MappedPath(
        this,
        mapper(mapping.asOut, failureMessage),
        mapping.asIn) documentation this::document

fun <A, B> OpenApiPath<A>.map(
    getter: (A) -> B,
    setter: (B) -> A): OpenApiPath<B> {
    return MappedPath(
        this,
        { Success(Match(getter(it.value), it.remaining)) },
        setter) documentation this::document
}
