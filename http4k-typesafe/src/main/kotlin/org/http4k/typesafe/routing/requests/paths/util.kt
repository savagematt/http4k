package org.http4k.typesafe.routing.requests.paths

val trailing = Regex("/*$")
val leading = Regex("^/*")
fun joinPaths(a: String, b: String) =
    a.replace(trailing, "") + "/" + b.replace(leading, "")

fun joinPaths(vararg paths: Path<*, *>) =
    paths.fold("/") { acc, path -> joinPaths(acc, path.toString()) }