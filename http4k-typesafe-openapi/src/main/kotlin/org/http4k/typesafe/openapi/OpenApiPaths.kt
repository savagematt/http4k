package org.http4k.typesafe.openapi

import org.http4k.typesafe.functional.Kind
import org.http4k.typesafe.routing.Paths
import org.http4k.typesafe.routing.joinPaths
import org.http4k.typesafe.routing.requests.paths.ConsumeUntil
import org.http4k.typesafe.routing.requests.paths.IgnoreFirst
import org.http4k.typesafe.routing.requests.paths.IgnoreSecond
import org.http4k.typesafe.routing.requests.paths.IndexInString
import org.http4k.typesafe.routing.requests.paths.Literal
import org.http4k.typesafe.routing.requests.paths.Mapped
import org.http4k.typesafe.routing.requests.paths.MappedPath
import org.http4k.typesafe.routing.requests.paths.Path
import java.time.format.DateTimeFormatter
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3

object OpenApiPaths : Paths<ForOpenApiPath> {
    override fun <T> Kind<ForOpenApiPath, T>.get(from: String) =
        this.fix().get(from)

    override fun <T> Kind<ForOpenApiPath, T>.set(into: String, value: T) =
        this.fix().set(into, value)

    override fun <A, B> Kind<ForOpenApiPath, A>.div(
        next: Kind<ForOpenApiPath, B>) =
        OpenApiPath2(this.fix(), next.fix())

    override infix fun <T> Kind<ForOpenApiPath, Unit>.but(next: Kind<ForOpenApiPath, T>) =
        IgnoreFirst(this.fix(), next.fix())
            .asOpenApi(fold(this.fix(), next.fix()))

    override infix fun <T> Kind<ForOpenApiPath, T>.ignore(next: Kind<ForOpenApiPath, Unit>) =
        IgnoreSecond(this.fix(), next.fix())
            .asOpenApi(fold(this.fix(), next.fix()))

    override fun consume(
        name: String, index: IndexInString) =
        ConsumeUntil(name, index).asOpenApi(pathParam(name))

    override fun literal(value: String) =
        Literal(value)
            .asOpenApi(operationPath(value))

    override fun Kind<ForOpenApiPath, String>.nonEmptyString() =
        wrap(Mapped::nonEmptyString, this)

    override fun Kind<ForOpenApiPath, String>.int() =
        wrap(Mapped::int, this)

    override fun Kind<ForOpenApiPath, String>.float() =
        wrap(Mapped::float, this)

    override fun Kind<ForOpenApiPath, String>.double() =
        wrap(Mapped::double, this)

    override fun Kind<ForOpenApiPath, String>.long() =
        wrap(Mapped::long, this)

    override fun Kind<ForOpenApiPath, String>.bigDecimal() =
        wrap(Mapped::bigDecimal, this)

    override fun Kind<ForOpenApiPath, String>.bigInteger() =
        wrap(Mapped::bigInteger, this)

    override fun Kind<ForOpenApiPath, String>.boolean() =
        wrap(Mapped::boolean, this)

    override fun Kind<ForOpenApiPath, String>.base64() =
        wrap(Mapped::base64, this)

    override fun Kind<ForOpenApiPath, String>.uuid() =
        wrap(Mapped::uuid, this)

    override fun Kind<ForOpenApiPath, String>.uri() =
        wrap(Mapped::uri, this)

    override fun Kind<ForOpenApiPath, String>.regex(pattern: String, group: Int) =
        wrap(Mapped::regex, this, pattern, group)

    override fun Kind<ForOpenApiPath, String>.regexObject() =
        wrap(Mapped::regexObject, this)

    override fun Kind<ForOpenApiPath, String>.duration() =
        wrap(Mapped::duration, this)

    override fun Kind<ForOpenApiPath, String>.yearMonth() =
        wrap(Mapped::yearMonth, this)

    override fun Kind<ForOpenApiPath, String>.instant() =
        wrap(Mapped::instant, this)

    override fun Kind<ForOpenApiPath, String>.dateTime(formatter: DateTimeFormatter) =
        wrap(Mapped::dateTime, this, formatter)

    override fun Kind<ForOpenApiPath, String>.zonedDateTime(formatter: DateTimeFormatter) =
        wrap(Mapped::zonedDateTime, this, formatter)

    override fun Kind<ForOpenApiPath, String>.localDate(formatter: DateTimeFormatter) =
        wrap(Mapped::localDate, this, formatter)

    override fun Kind<ForOpenApiPath, String>.localTime(formatter: DateTimeFormatter) =
        wrap(Mapped::localTime, this, formatter)
}

fun <T> Path<T>.asOpenApi(
    docs: (OpenApiRouteInfo) -> OpenApiRouteInfo = { it }) =
    OpenApiPath(this, docs)

fun operationPath(value: String): (OpenApiRouteInfo) -> OpenApiRouteInfo = {
    it.path { path ->
        joinPaths(path, value)
    }
}

private fun pathParam(name: String): (OpenApiRouteInfo) -> OpenApiRouteInfo = { info ->
    info.path { path ->
        joinPaths(path, "{$name}")
    }.parameters {
        it + OpenApiParameter(ParameterLocation.PATH, name).real()
    }
}

/**
 * Uses wrapper to wrap path in a MappedPath,
 * but preserves the behaviour of path.document()
 */
private fun <T> wrap(wrapper: KFunction1<Path<String>, MappedPath<String, T>>,
                     path: Kind<ForOpenApiPath, String>): Kind<ForOpenApiPath, T> =
    wrapper(path.fix())
        .asOpenApi(fold(path.fix()))

/**
 * Uses wrapper to wrap path in a MappedPath,
 * but preserves the behaviour of path.document()
 */
private fun <T, A, B> wrap(wrapper: KFunction3<Path<String>, A, B, MappedPath<String, T>>,
                           path: Kind<ForOpenApiPath, String>,
                           a: A,
                           b: B): Kind<ForOpenApiPath, T> =
    wrapper(path.fix(), a, b)
        .asOpenApi(fold(path.fix()))

/**
 * Uses wrapper to wrap path in a MappedPath,
 * but preserves the behaviour of path.document()
 */
private fun <T, A> wrap(wrapper: KFunction2<Path<String>, A, MappedPath<String, T>>,
                        path: Kind<ForOpenApiPath, String>,
                        a: A): Kind<ForOpenApiPath, T> =
    wrapper(path.fix(), a)
        .asOpenApi(fold(path.fix()))
