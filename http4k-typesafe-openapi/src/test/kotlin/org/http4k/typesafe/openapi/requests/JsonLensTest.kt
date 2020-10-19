package org.http4k.typesafe.openapi.requests

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.customise
import org.http4k.typesafe.openapi.containsString
import org.http4k.typesafe.openapi.routing.at
import org.http4k.typesafe.openapi.routing.request
import org.http4k.typesafe.openapi.routing.with
import org.http4k.typesafe.routing.RoutingError
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@Suppress("MemberVisibilityCanBePrivate")
class JsonLensTest {
    val json = ConfigurableJackson(KotlinModule().asConfigurable().customise())
    val jsonString = """{"parent":{"child":1234}}"""

    val lens = GET at "/widget" with request.json(json)

    @Test
    fun `can get json`() {
        assertThat(
            lens.get(Request(GET, "/widget").body(jsonString)),
            equalTo<Result<JsonNode, RoutingError>>(Success(json.parse(jsonString)))
        )
    }

    @Test
    fun `reports incorrect json`() {
        when (val actual = lens.get(Request(GET, "/widget").body("}not json"))) {
            is Success -> fail("Expected route failure")
            is Failure -> {
                assertThat(
                    actual.reason.message,
                    String::containsString,
                    "Unexpected close marker"
                )
            }
        }
    }


    @Test
    fun `can set json`() {
        assertThat(
            lens.set(Request(GET, "/"), json.parse(jsonString)),
            equalTo<Result<Request, RoutingError>>(Success(
                Request(GET, "/widget")
                    .header("Content-Type", APPLICATION_JSON.toHeaderValue())
                    .body(jsonString)))
        )
    }


}