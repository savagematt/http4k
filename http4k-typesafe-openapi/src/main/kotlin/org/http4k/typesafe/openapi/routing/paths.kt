package org.http4k.typesafe.openapi.routing

import org.http4k.openapi.OpenApiParameter
import org.http4k.openapi.OpenApiRouteInfo
import org.http4k.openapi.ParameterLocation
import org.http4k.openapi.builders.OpenApiRouteInfoDsl
import org.http4k.openapi.real
import org.http4k.typesafe.openapi.OpenApiPath
import org.http4k.typesafe.openapi.OpenApiPath2
import org.http4k.typesafe.routing.requests.paths.joinPaths
import org.http4k.typesafe.routing.requests.paths.ConsumeUntil
import org.http4k.typesafe.routing.requests.paths.IgnoreFirst
import org.http4k.typesafe.routing.requests.paths.IgnoreSecond
import org.http4k.typesafe.routing.requests.paths.IndexInString
import org.http4k.typesafe.routing.requests.paths.Literal
import org.http4k.typesafe.routing.requests.paths.Mapped
import org.http4k.typesafe.routing.requests.paths.MappedPath
import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.nextSlash
import org.http4k.util.fold
import java.time.format.DateTimeFormatter
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3

operator fun <A, B> OpenApiPath<A>.div(
    next: OpenApiPath<B>) =
    OpenApiPath2(this, next)

operator fun <T> String.div(
    next: OpenApiPath<T>):OpenApiPath<T> =
    literal(this) but next

operator fun <T> OpenApiPath<T>.div(
    next: String) =
    this ignore literal(next)

infix fun <T> OpenApiPath<Unit>.but(next: OpenApiPath<T>) =
    IgnoreFirst(this, next) documentation fold(this, next)

infix fun <T> OpenApiPath<T>.ignore(next: OpenApiPath<Unit>) =
    IgnoreSecond(this, next) documentation fold(this, next)

fun consume(
    name: String, index: IndexInString = ::nextSlash) =
    ConsumeUntil(name, index) openapi pathParam(name)

fun literal(value: String) =
    Literal(value) openapi operationPath(value)

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

infix fun <T> Path<T>.openapi(
    docs: (OpenApiRouteInfoDsl.() -> Unit)) =
    this documentation { OpenApiRouteInfoDsl(it).also(docs).build() }

infix fun <T> Path<T>.documentation(
    docs: (OpenApiRouteInfo) -> OpenApiRouteInfo) =
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
private fun <T> wrap(wrapper: KFunction1<Path<String>, MappedPath<String, T>>,
                     path: OpenApiPath<String>): OpenApiPath<T> =
    wrapper(path) documentation fold(path)

/**
 * Uses wrapper to wrap path in a MappedPath,
 * but preserves the behaviour of path.document()
 */
private fun <T, A, B> wrap(wrapper: KFunction3<Path<String>, A, B, MappedPath<String, T>>,
                           path: OpenApiPath<String>,
                           a: A,
                           b: B): OpenApiPath<T> =
    wrapper(path, a, b) documentation fold(path)

/**
 * Uses wrapper to wrap path in a MappedPath,
 * but preserves the behaviour of path.document()
 */
private fun <T, A> wrap(wrapper: KFunction2<Path<String>, A, MappedPath<String, T>>,
                        path: OpenApiPath<String>,
                        a: A): OpenApiPath<T> =
    wrapper(path, a) documentation fold(path)
