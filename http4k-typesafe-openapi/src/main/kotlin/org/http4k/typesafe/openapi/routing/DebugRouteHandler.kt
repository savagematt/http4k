package org.http4k.typesafe.openapi.routing

import org.http4k.core.Headers
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.openapi.OpenApiOperationInfo
import org.http4k.openapi.OpenApiRouteDocs
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.RoutingError
import java.io.PrintWriter
import java.io.StringWriter

data class RoutingFailure(val route: Route<*, *, OpenApiRouteDocs>, val error: RoutingError.WrongRoute)

private const val RESET = "\u001B[0m"
private const val BLACK = "\u001B[30m"
private const val RED = "\u001B[31m"
private const val GREEN = "\u001B[32m"
private const val YELLOW = "\u001B[33m"
private const val BLUE = "\u001B[34m"
private const val PURPLE = "\u001B[35m"
private const val CYAN = "\u001B[36m"
private const val RED_BACKGROUND = "\u001B[41m"

private fun colour(color: String, text: String) {
    if(text.isNotBlank()) {
        println("$color$text$RESET")
    }
}

private fun headers(headers: Headers) {
    if(headers.isNotEmpty()) {
        println(headers.joinToString("\r\n") { "$GREEN${it.first}$RESET: $BLUE${it.second}$RESET" })
    }
}

private fun statusColour(status: Status) =
    when {
        (200..299).contains(status.code) -> GREEN
        (300..399).contains(status.code) -> YELLOW
        (400..499).contains(status.code) -> RED
        (500..599).contains(status.code) -> "$RED_BACKGROUND$BLACK"
        else -> CYAN
    }

private fun routeName(operation: OpenApiOperationInfo) =
    "$GREEN${operation.method} $BLUE${operation.path}${operation.operation.description?.let { " $CYAN$it" }
        ?: ""}$RESET"

private fun operation(route: Route<*, *, OpenApiRouteDocs>): OpenApiOperationInfo =
    route.document(OpenApiRouteDocs.empty).operation

private fun stackTrace(e: Exception): String =
    StringWriter()
        .also { e.printStackTrace(PrintWriter(it)) }
        .toString()

/**
 * Useful in tests. Prints extremely verbose routing information with colouration to the console.
 */
class DebugRouteHandler : StatefulRouteHandler() {
    override fun handle(
        request: Request,
        unmatched: List<RoutingFailure>,
        matched: Route<*, *, OpenApiRouteDocs>?,
        error: RoutingError.RouteFailed?,
        response: Response
    ): Response {

        val didMatch = matched != null && error == null
        val markerColour = if (didMatch) GREEN else RED
        val stackTrace = error?.exception?.let(::stackTrace)

        fun marker(title: String, char: String = "-") = println("\n$markerColour${char.repeat(100 - title.length)} $title $char$char$RESET")

        // REQUEST
        println()
        marker("request", "=")

        colour(GREEN, "${request.method} $BLUE${request.uri} $CYAN${request.version}")
        headers(request.headers)
        colour(PURPLE, request.bodyString())

        // MATCHED ROUTE
        marker("match")
        if (matched == null) {
            colour(RED, "NOT FOUND to match any of ${unmatched.size} routes")
        } else {
            val routeName = routeName(operation(matched))
            if (error != null) {
                val trace = (stackTrace?.let { "\n$it" }?: "")
                colour(RED, "FAILED $routeName\n$RED${error.message}\n$RED$trace")
            } else {
                colour(CYAN, "MATCH $routeName")
            }
        }

        // RESPONSE
        marker("response")

        println("$CYAN${response.version} ${statusColour(response.status)}${response.status.code}$RESET")
        headers(response.headers)
        colour(PURPLE, response.bodyString())

        // UNMATCHED ROUTES
        if (unmatched.isNotEmpty()) {
            marker("unmatched")

            unmatched.forEach {
                val operation = operation(it.route)
                println("${routeName(operation)}\n${" ".repeat(operation.method.name.length + 1)}$RED${it.error.message}$RESET")
            }
        }

        return when {
            matched != null ->
                response

            // On error, set response body to error message and stack trace
            error != null ->
                error.response.body("${error.message}${stackTrace?.let { "\n$it" } ?: ""}")

            // On 404, set response body to readable list of route matching failures
            else ->
                response.body(unmatched
                    .joinToString("\r\n") { "${routeName(operation(it.route))}\n     ${it.error.message}" }
                )
        }
    }
}