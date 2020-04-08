package org.http4k.typesafe.routing.requests.paths

interface SimplePath<T> : Path<T>{
    operator fun div(next: String) = this / Literal(next)
    operator fun div(next: Literal) = IgnoreSecond(this, next)
}