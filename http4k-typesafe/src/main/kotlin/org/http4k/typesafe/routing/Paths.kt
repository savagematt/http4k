package org.http4k.typesafe.routing

import org.http4k.core.Uri
import org.http4k.typesafe.data.Tuple2
import org.http4k.typesafe.functional.Kind
import org.http4k.typesafe.routing.requests.paths.IndexInString
import org.http4k.typesafe.routing.requests.paths.Path
import org.http4k.typesafe.routing.requests.paths.PathResult
import org.http4k.typesafe.routing.requests.paths.leading
import org.http4k.typesafe.routing.requests.paths.nextSlash
import org.http4k.typesafe.routing.requests.paths.trailing
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME
import java.util.*


fun joinPaths(a: String, b: String) =
    a.replace(trailing, "") + "/" + b.replace(leading, "")

fun joinPaths(vararg paths: Path<*>) =
    paths.fold("/") { acc, path -> joinPaths(acc, path.toString()) }

interface Paths<TPath> {
    fun <T> Kind<TPath, T>.get(from: String): PathResult<T>

    fun <T> Kind<TPath, T>.set(into: String, value: T): String

    operator fun String.div(path: String) =
        joinPaths(this, path)

    operator fun <T> String.div(next: Kind<TPath, T>) =
        literal(this) but next

    operator fun <T> Kind<TPath, T>.div(next: String) =
        this ignore literal(next)

    operator fun <A, B> Kind<TPath, A>.div(next: Kind<TPath, B>): Kind<TPath, Tuple2<A, B>>

    infix fun <T> Kind<TPath, Unit>.but(next: Kind<TPath, T>): Kind<TPath, T>

    infix fun <T> Kind<TPath, T>.ignore(next: Kind<TPath, Unit>): Kind<TPath, T>

    fun consume(name: String, index: IndexInString = ::nextSlash): Kind<TPath, String>

    fun literal(value: String): Kind<TPath, Unit>

    fun Kind<TPath, String>.nonEmptyString():
        Kind<TPath, String>

    fun Kind<TPath, String>.int():
        Kind<TPath, Int>

    fun Kind<TPath, String>.float():
        Kind<TPath, Float>

    fun Kind<TPath, String>.double():
        Kind<TPath, Double>

    fun Kind<TPath, String>.long():
        Kind<TPath, Long>

    fun Kind<TPath, String>.bigDecimal():
        Kind<TPath, BigDecimal>

    fun Kind<TPath, String>.bigInteger():
        Kind<TPath, BigInteger>

    fun Kind<TPath, String>.boolean():
        Kind<TPath, Boolean>

    fun Kind<TPath, String>.base64():
        Kind<TPath, String>

    fun Kind<TPath, String>.uuid():
        Kind<TPath, UUID>

    fun Kind<TPath, String>.uri():
        Kind<TPath, Uri>

    fun Kind<TPath, String>.regex(pattern: String, group: Int = 1):
        Kind<TPath, String>

    fun Kind<TPath, String>.regexObject():
        Kind<TPath, Regex>

    fun Kind<TPath, String>.duration():
        Kind<TPath, Duration>

    fun Kind<TPath, String>.yearMonth():
        Kind<TPath, YearMonth>

    fun Kind<TPath, String>.instant():
        Kind<TPath, Instant>

    fun Kind<TPath, String>.dateTime(formatter: DateTimeFormatter = ISO_LOCAL_DATE_TIME):
        Kind<TPath, LocalDateTime>

    fun Kind<TPath, String>.zonedDateTime(formatter: DateTimeFormatter = ISO_ZONED_DATE_TIME):
        Kind<TPath, ZonedDateTime>

    fun Kind<TPath, String>.localDate(formatter: DateTimeFormatter = ISO_LOCAL_DATE):
        Kind<TPath, LocalDate>

    fun Kind<TPath, String>.localTime(formatter: DateTimeFormatter = ISO_LOCAL_TIME):
        Kind<TPath, LocalTime>
}