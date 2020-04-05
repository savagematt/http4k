package org.http4k.typesafe.routing.requests

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.customise
import org.http4k.typesafe.routing.RoutingError
import org.http4k.typesafe.routing.RoutingError.Companion.routeFailed
import org.http4k.typesafe.routing.Simple.but
import org.http4k.typesafe.routing.SimpleRequestRouting.bind
import org.http4k.typesafe.routing.SimpleRequestRouting.json
import org.junit.jupiter.api.Test

@Suppress("MemberVisibilityCanBePrivate")
class JsonLensTest {
    val json = ConfigurableJackson(KotlinModule().asConfigurable().customise())
    val jsonString = """{"parent":{"child":1234}}"""

    val lens = GET bind "/widget" but json(json)

    @Test
    fun `can get json`() {
        assertThat(
            lens.get(Request(GET, "/widget").body(jsonString)),
            equalTo<Result<JsonNode, RoutingError>>(Success(json.parse(jsonString)))
        )
    }

    @Test
    fun `reports incorrect json`() {
        assertThat(
            lens.get(Request(GET, "/widget").body("}not json")),
            equalTo<Result<JsonNode, RoutingError>>(
                routeFailed(BAD_REQUEST,
                    """Invalid json. Unexpected close marker '}': expected ']' (for root starting at [Source: (String)"}not json"; line: 1, column: 0])
 at [Source: (String)"}not json"; line: 1, column: 2]"""))
        )
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