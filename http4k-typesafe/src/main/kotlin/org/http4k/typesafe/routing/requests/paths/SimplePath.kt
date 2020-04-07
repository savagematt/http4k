package org.http4k.typesafe.routing.requests.paths

import org.http4k.util.functional.Kind


/** @see [org.http4k.util.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
class ForSimplePath private constructor() {
    companion object
}

/** @see [org.http4k.util.functional.Kind2]
 *  or https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds */
fun <T> Kind<ForSimplePath, T>.fix() = this as SimplePath<T>

interface SimplePath<T> : Path<T>, Kind<ForSimplePath, T>