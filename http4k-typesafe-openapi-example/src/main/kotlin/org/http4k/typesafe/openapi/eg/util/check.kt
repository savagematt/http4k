package org.http4k.typesafe.openapi.eg.util

import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.flatMap
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.typesafe.openapi.eg.HasId
import org.http4k.typesafe.openapi.routing.LensWrapper
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.http4k.util.data.Tuple2
import org.http4k.util.data.tuple

fun <Id, T : HasId<Id>> correctId(): LensWrapper<Request, Tuple2<Id, T>, T, OpenApiRouteDocs> =
    object : LensWrapper<Request, Tuple2<Id, T>, T, OpenApiRouteDocs> {
        override fun invoke(wrapped: MessageLens<Request, Tuple2<Id, T>, OpenApiRouteDocs>): MessageLens<Request, T, OpenApiRouteDocs> {
            return IdMatchesLens(wrapped)
        }
    }

class IdMatchesLens<Id, T : HasId<Id>, D>(val wrapped: MessageLens<Request, Tuple2<Id, T>, D>) : MessageLens<Request, T, D> {
    override fun get(from: Request): Result<T, RoutingError> =
        wrapped
            .get(from)
            .flatMap {
                if (it.a == it.b.id)
                    Success(it.b)
                else
                    routeFailed("Resource id in url does not match id in body payload", Status.BAD_REQUEST)
            }

    override fun set(into: Request, value: T): Result<Request, RoutingError> =
        wrapped.set(into, tuple(value.id, value))

    override fun document(doc: D): D = wrapped.document(doc)
}
