package org.http4k.security.oauth.server.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import org.http4k.core.Uri
import org.http4k.security.ResponseMode
import org.http4k.security.ResponseType
import org.http4k.security.State
import org.http4k.security.oauth.server.ClientId
import org.http4k.security.openid.Nonce

data class RequestObject(val client: ClientId? = null,
                         val redirectUri: Uri? = null,
                         val audience: List<String> = emptyList(),
                         val issuer: String? = null,
                         val scope: List<String> = emptyList(),
                         val responseMode: ResponseMode? = null,
                         val responseType: ResponseType? = null,
                         val state: State? = null,
                         val nonce: Nonce? = null,
                         val magAge: Long? = null,
                         val expiry: Long? = null,
                         val claims: Claims = Claims())

data class Claims(@JsonProperty("userinfo") val userInfo: Map<String, Claim>? = null,
                  @JsonProperty("id_token") val idToken: Map<String, Claim>? = null)

data class Claim(val essential: Boolean = false, val value: String? = null, val values: List<String>? = null)

