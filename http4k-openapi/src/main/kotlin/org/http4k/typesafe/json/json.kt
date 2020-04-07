package org.http4k.typesafe.json

import org.http4k.format.Json

interface JsonRenderer<T, NODE> : Json<NODE> {
    fun render(value: T): NODE
}

interface Renderable {
    fun <NODE> render(json: Json<NODE>): NODE
}

interface Extension {
    fun <NODE> render(base: NODE, json: Json<NODE>): NODE
}