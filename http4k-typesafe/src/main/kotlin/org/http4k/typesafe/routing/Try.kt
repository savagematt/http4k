package org.http4k.typesafe.routing

import com.natpryce.Result

/**
 * Some lenses catch exceptions and return either a [RoutingError] or a successful
 * result.
 *
 * Usually the lens will not expose the stack trace of the exception. This can make
 * it tricky to debug issues.
 *
 * By injecting [Try] into lenses that handle known exceptions, we can choose to log
 * the stack trace to stderr, or elsewhere.
 */
interface Try {
    operator fun <T> invoke(f: () -> Result<T, RoutingError>,
                            e: (Exception) -> Result<T, RoutingError>): Result<T, RoutingError>
}

object PrintStackTrace : Try {
    override fun <T> invoke(f: () -> Result<T, RoutingError>, e: (Exception) -> Result<T, RoutingError>): Result<T, RoutingError> =
        try {
            f()
        } catch (e: Exception) {
            e.printStackTrace()
            e(e)
        }

}

object SwallowException : Try {
    override fun <T> invoke(f: () -> Result<T, RoutingError>, e: (Exception) -> Result<T, RoutingError>): Result<T, RoutingError> =
        try {
            f()
        } catch (e: Exception) {
            e(e)
        }
}