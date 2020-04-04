package org.http4k.typesafe.routing

import org.http4k.typesafe.functional.Kind
import org.http4k.typesafe.routing.requests.paths.ConsumeUntil
import org.http4k.typesafe.routing.requests.paths.ForSimplePath
import org.http4k.typesafe.routing.requests.paths.IgnoreSecond
import org.http4k.typesafe.routing.requests.paths.IndexInString
import org.http4k.typesafe.routing.requests.paths.Literal
import org.http4k.typesafe.routing.requests.paths.Mapped
import org.http4k.typesafe.routing.requests.paths.Path2
import org.http4k.typesafe.routing.requests.paths.fix
import java.time.format.DateTimeFormatter

object SimplePaths : Paths<ForSimplePath> {

    override fun <T> Kind<ForSimplePath, T>.div(
        next: String): Kind<ForSimplePath, T> =
        IgnoreSecond(fix(), literal(next).fix())

    override fun <A, B> Kind<ForSimplePath, A>.div(
        next: Kind<ForSimplePath, B>) =
        Path2(this.fix(), next.fix())

    override fun consume(
        name: String, index: IndexInString) =
        ConsumeUntil(name, index)

    override fun literal(value: String) =
        Literal(value)

    override fun Kind<ForSimplePath, String>.nonEmptyString() =
        Mapped.nonEmptyString(this.fix())

    override fun Kind<ForSimplePath, String>.int() =
        Mapped.int(this.fix())

    override fun Kind<ForSimplePath, String>.float() =
        Mapped.float(this.fix())

    override fun Kind<ForSimplePath, String>.double() =
        Mapped.double(this.fix())

    override fun Kind<ForSimplePath, String>.long() =
        Mapped.long(this.fix())

    override fun Kind<ForSimplePath, String>.bigDecimal() =
        Mapped.bigDecimal(this.fix())

    override fun Kind<ForSimplePath, String>.bigInteger() =
        Mapped.bigInteger(this.fix())

    override fun Kind<ForSimplePath, String>.boolean() =
        Mapped.boolean(this.fix())

    override fun Kind<ForSimplePath, String>.base64() =
        Mapped.base64(this.fix())

    override fun Kind<ForSimplePath, String>.uuid() =
        Mapped.uuid(this.fix())

    override fun Kind<ForSimplePath, String>.uri() =
        Mapped.uri(this.fix())

    override fun Kind<ForSimplePath, String>.regex(pattern: String, group: Int) =
        Mapped.regex(this.fix(), pattern, group)

    override fun Kind<ForSimplePath, String>.regexObject() =
        Mapped.regexObject(this.fix())

    override fun Kind<ForSimplePath, String>.duration() =
        Mapped.duration(this.fix())

    override fun Kind<ForSimplePath, String>.yearMonth() =
        Mapped.yearMonth(this.fix())

    override fun Kind<ForSimplePath, String>.instant() =
        Mapped.instant(this.fix())

    override fun Kind<ForSimplePath, String>.dateTime(formatter: DateTimeFormatter) =
        Mapped.dateTime(this.fix(), formatter)

    override fun Kind<ForSimplePath, String>.zonedDateTime(formatter: DateTimeFormatter) =
        Mapped.zonedDateTime(this.fix(), formatter)

    override fun Kind<ForSimplePath, String>.localDate(formatter: DateTimeFormatter) =
        Mapped.localDate(this.fix(), formatter)

    override fun Kind<ForSimplePath, String>.localTime(formatter: DateTimeFormatter) =
        Mapped.localTime(this.fix(), formatter)
}