package org.http4k.typesafe.routing.security

import com.natpryce.Result
import com.natpryce.Success
import org.http4k.core.Credentials
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.typesafe.routing.RoutingError

fun basicAuthValidator(realm:String, validator: (Credentials) -> Boolean): (Credentials) -> Result<String, RoutingError> = {
    if (validator(it))
        Success(it.user)
    else
        RoutingError.routeFailed(
            "Invalid credentials",
            Response(Status.UNAUTHORIZED).header("WWW-Authenticate", "Basic Realm=\"$realm\""))
}