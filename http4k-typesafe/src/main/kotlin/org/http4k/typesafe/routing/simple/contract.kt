package org.http4k.typesafe.routing.simple

import org.http4k.core.HttpMessage
import org.http4k.typesafe.functional.Kind2
import org.http4k.typesafe.routing.MessageLens

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
class ForSimpleLens private constructor() {
    companion object
}

/** @see [org.http4k.typesafe.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <M : HttpMessage, T> Kind2<ForSimpleLens, M, T>.fix() = this as SimpleLens<M, T>

interface SimpleLens<M : HttpMessage, T> : MessageLens<M, T>, Kind2<ForSimpleLens, M, T>