package org.http4k.typesafe.openapi

import org.http4k.format.Json

interface JsonRenderer<T, NODE> : Json<NODE> {
    fun render(value: T): NODE
}

interface Renderable {
    fun <NODE> render(json: Json<NODE>): NODE
}

fun <NODE> Json<NODE>.nullable(value: String?) =
    nullable(value, { this.string(it) })

fun <NODE> Json<NODE>.nullable(value: Boolean?) =
    nullable(value, { this.boolean(it) })

fun <NODE> Json<NODE>.nullable(value: Renderable?) =
    nullable(value, { it.render(this) })

fun <T, NODE, R : Json<NODE>> R.nullable(value: T?, toNode: R.(T) -> NODE) =
    when (value) {
        null -> this.nullNode()
        else -> this.toNode(value)
    }

fun <T : C, C, NODE> JsonRenderer<C, NODE>.nullable(value: T?) =
    nullable(value, { this.render(it) })

fun <T : C, C, NODE> JsonRenderer<C, NODE>.list(values: List<T>) =
    this.array(values.map(this::render))

fun <T : C, C, NODE> JsonRenderer<C, NODE>.nullable(values: List<T>?) =
    nullable(values, { this.list(it) })

fun <K, T : C, C, NODE> JsonRenderer<C, NODE>.obj(value: Map<K, T>, keyFn: (K) -> String = { it.toString() }) =
    obj(value.map { keyFn(it.key) to render(it.value) })

fun <K, T : C, C, NODE> JsonRenderer<C, NODE>.nullableObj(value: Map<K, T>?, keyFn: (K) -> String = { it.toString() }) =
    nullable(value, { this.obj(it, keyFn) })