package org.http4k.typesafe.openapi

import org.http4k.typesafe.functional.Kind
import org.http4k.typesafe.routing.Paths
import org.http4k.typesafe.routing.requests.paths.ConsumeUntil
import org.http4k.typesafe.routing.requests.paths.IgnoreSecond
import org.http4k.typesafe.routing.requests.paths.IndexInString
import org.http4k.typesafe.routing.requests.paths.Literal
import org.http4k.typesafe.routing.requests.paths.Mapped
import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.div
import java.time.format.DateTimeFormatter

object OpenApiPaths : Paths<ForOpenApiPath> {
    override fun <T> Kind<ForOpenApiPath, T>.div(
        next: String): Kind<ForOpenApiPath, T> {
        val path = this.fix()
        return IgnoreSecond(path, literal(next).fix())
            .asOpenApi()
    }

    override fun <A, B> Kind<ForOpenApiPath, A>.div(
        next: Kind<ForOpenApiPath, B>) =
        OpenApiPath2(this.fix(), next.fix())

    override fun consume(
        name: String, index: IndexInString) =
        ConsumeUntil(name, index).asOpenApi { doc -> doc.path { it / "{${name}}" } }

    override fun literal(value: String) =
        Literal(value)
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.nonEmptyString() =
        Mapped.nonEmptyString(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.int() =
        Mapped.int(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.float() =
        Mapped.float(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.double() =
        Mapped.double(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.long() =
        Mapped.long(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.bigDecimal() =
        Mapped.bigDecimal(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.bigInteger() =
        Mapped.bigInteger(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.boolean() =
        Mapped.boolean(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.base64() =
        Mapped.base64(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.uuid() =
        Mapped.uuid(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.uri() =
        Mapped.uri(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.regex(pattern: String, group: Int) =
        Mapped.regex(this.fix(), pattern, group)
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.regexObject() =
        Mapped.regexObject(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.duration() =
        Mapped.duration(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.yearMonth() =
        Mapped.yearMonth(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.instant() =
        Mapped.instant(this.fix())
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.dateTime(formatter: DateTimeFormatter) =
        Mapped.dateTime(this.fix(), formatter)
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.zonedDateTime(formatter: DateTimeFormatter) =
        Mapped.zonedDateTime(this.fix(), formatter)
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.localDate(formatter: DateTimeFormatter) =
        Mapped.localDate(this.fix(), formatter)
            .asOpenApi()

    override fun Kind<ForOpenApiPath, String>.localTime(formatter: DateTimeFormatter) =
        Mapped.localTime(this.fix(), formatter)
            .asOpenApi()
}

fun <T> Path<T>.asOpenApi(
    docs: (OpenApiRouteInfo) -> OpenApiRouteInfo = { it }) =
    OpenApiPath(this, docs)
