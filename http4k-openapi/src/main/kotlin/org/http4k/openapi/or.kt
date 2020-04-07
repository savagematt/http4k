package org.http4k.typesafe.openapi

import org.http4k.openapi.OpenApiConcept
import org.http4k.openapi.OpenApiContact
import org.http4k.openapi.OpenApiInfo
import org.http4k.openapi.OpenApiLicense
import org.http4k.openapi.OpenApiRouteInfo
import org.http4k.openapi.Referenceable
import org.http4k.util.merging.Merge
import org.http4k.util.merging.changes
import org.http4k.util.merging.join

//
//
//infix fun OpenApiInfo.or(other: OpenApiInfo) =
//    OpenApiInfo.empty.changes(other, this) {
//        OpenApiInfo(
//            latest { it.title },
//            latest { it.version },
//            latest { it.description },
//            latest { it.termsOfService },
//            changes(OpenApiContact.empty) {it.termsOfService},
//
//
//            )
//    }
//
//infix fun List<OpenApiOperationInfo>.or(other: List<OpenApiOperationInfo>): List<OpenApiOperationInfo> {
//
//}

infix fun OpenApiRouteInfo.or(other: OpenApiRouteInfo): OpenApiRouteInfo {
    TODO()
//    return OpenApiRouteInfo.empty.changes(this, other) {
//        OpenApiRouteInfo(
//            latest({ it.api }) {
//                OpenApiObject(
//                    b.info or a.info,
//                    join { it.paths },
//                    latest({ it.components }) {
//                        OpenApiComponents(
//                            mergeNullable({ it.securitySchemes }, OpenApiHttpSecurity.empty.real()) {
//                                latest { it }
//                            },
//                            combine({ it.schemas }, ::merge),
//
//                            )
//                    },
//                    )
//            }
//    }
}


infix fun OpenApiInfo.or(other: OpenApiInfo): OpenApiInfo =
    OpenApiInfo.empty.changes(this, other).run {
        OpenApiInfo(
            latest { it.title },
            latest { it.version },
            latest { it.description },
            latest { it.termsOfService },
            latest({ it.contact }, OpenApiContact.empty) {
                OpenApiContact(
                    latest { it.name },
                    latest { it.url },
                    latest { it.email },
                    join { it.extensions }
                )
            },
            latest({ it.license }, OpenApiLicense.empty) {
                OpenApiLicense(
                    latest { it.name },
                    latest { it.url },
                    join { it.extensions }
                )
            },
            join { it.extensions }
        )
    }

fun <T, V : OpenApiConcept> Merge<T>.mergeReferenceable(
    getter: (T) -> Referenceable<V>,
    nullValue: V,
    f: Merge<V>.() -> Referenceable<V>
): Referenceable<V> {
    TODO()
//    val ref = getter(reference)
//    val b = getter(b)
//    val a = getter(a)
//
//    return when (b) {
//        is Ref -> when (a) {
//            is Ref -> a
//            else -> ref.changes(ref, a).merge({ it }, nullValue, f)
//        }
//        else -> when (a) {
//            is Ref -> ref.changes(b, ref).merge({ it }, nullValue, f)
//            else -> ref.changes(b, a).merge({ it }, nullValue, f)
//        }
//    }
}
