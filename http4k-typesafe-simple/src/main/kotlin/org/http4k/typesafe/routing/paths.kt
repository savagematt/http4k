package org.http4k.typesafe.routing

import org.http4k.typesafe.routing.requests.paths.ConsumeUntil
import org.http4k.typesafe.routing.requests.paths.IgnoreFirst
import org.http4k.typesafe.routing.requests.paths.IgnoreSecond
import org.http4k.typesafe.routing.requests.paths.IndexInString
import org.http4k.typesafe.routing.requests.paths.Literal
import org.http4k.typesafe.routing.requests.paths.Mapped
import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.Path2
import org.http4k.typesafe.routing.requests.paths.nextSlash
import java.time.format.DateTimeFormatter

operator fun <T> String.div(
    next: Path<T>) =
    IgnoreFirst(Literal(this), next)

operator fun <A, B> Path<A>.div(
    next: Path<B>) =
    Path2(this, next)

fun consume(
    name: String, index: IndexInString = ::nextSlash) =
    ConsumeUntil(name, index)

fun literal(value: String) =
    Literal(value)

fun Path<String>.nonEmptyString() =
    Mapped.nonEmptyString(this)

fun Path<String>.int() =
    Mapped.int(this)

fun Path<String>.float() =
    Mapped.float(this)

fun Path<String>.double() =
    Mapped.double(this)

fun Path<String>.long() =
    Mapped.long(this)

fun Path<String>.bigDecimal() =
    Mapped.bigDecimal(this)

fun Path<String>.bigInteger() =
    Mapped.bigInteger(this)

fun Path<String>.boolean() =
    Mapped.boolean(this)

fun Path<String>.base64() =
    Mapped.base64(this)

fun Path<String>.uuid() =
    Mapped.uuid(this)

fun Path<String>.uri() =
    Mapped.uri(this)

fun Path<String>.regex(pattern: String, group: Int) =
    Mapped.regex(this, pattern, group)

fun Path<String>.regexObject() =
    Mapped.regexObject(this)

fun Path<String>.duration() =
    Mapped.duration(this)

fun Path<String>.yearMonth() =
    Mapped.yearMonth(this)

fun Path<String>.instant() =
    Mapped.instant(this)

fun Path<String>.dateTime(formatter: DateTimeFormatter) =
    Mapped.dateTime(this, formatter)

fun Path<String>.zonedDateTime(formatter: DateTimeFormatter) =
    Mapped.zonedDateTime(this, formatter)

fun Path<String>.localDate(formatter: DateTimeFormatter) =
    Mapped.localDate(this, formatter)

fun Path<String>.localTime(formatter: DateTimeFormatter) =
    Mapped.localTime(this, formatter)
