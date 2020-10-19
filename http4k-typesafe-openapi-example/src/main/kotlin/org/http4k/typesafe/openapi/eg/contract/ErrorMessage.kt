package org.http4k.typesafe.openapi.eg.contract

import org.http4k.typesafe.openapi.eg.Value
import org.http4k.typesafe.openapi.eg.map
import org.http4k.typesafe.openapi.routing.response

/**
 * Intended to be human readable
 */
class ErrorMessage(value: String) : Value<String>(value)

fun errorMessage() = response.text().map(::ErrorMessage)