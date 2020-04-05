package org.http4k.typesafe.openapi

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK
import org.http4k.format.ConfigurableJackson
import org.http4k.format.Json
import org.http4k.format.asConfigurable
import org.http4k.format.customise
import org.http4k.testing.Approver
import org.http4k.testing.JsonApprovalTest
import org.http4k.typesafe.json.JsonRenderer
import org.http4k.typesafe.openapi.OpenApiPaths.boolean
import org.http4k.typesafe.openapi.OpenApiPaths.consume
import org.http4k.typesafe.openapi.OpenApiPaths.div
import org.http4k.typesafe.openapi.OpenApiRequestRouting.bind
import org.http4k.typesafe.openapi.OpenApiRequestRouting.header
import org.http4k.typesafe.openapi.OpenApiRequestRouting.required
import org.http4k.typesafe.openapi.OpenApiResponseRouting.with
import org.http4k.typesafe.openapi.OpenApiRouting.and
import org.http4k.typesafe.openapi.OpenApiRouting.but
import org.http4k.typesafe.openapi.OpenApiRouting.request
import org.http4k.typesafe.openapi.OpenApiRouting.response
import org.http4k.typesafe.openapi.OpenApiRouting.route
import org.http4k.typesafe.openapi.schema.induceSchema
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(JsonApprovalTest::class)
class V3RendererTest {
    val json = ConfigurableJackson(KotlinModule().asConfigurable().customise())

    @Test
    fun `description`(approver: Approver) {
        val routes: List<OpenApiRoute<*, *>> = listOf(
            route(
                GET bind "/nometa",
                response.any()
            ),
            route(
                GET bind "/descriptions" meta {
                    summary = "endpoint"
                    description = "some rambling description of what this thing actually does"
                    tags += Tag("tag3")
                    tags += Tag("tag1")
                    deprecated = true
                },
                response.any()
            ),
            route(
                POST bind "/paths" / consume("firstName") / "bertrand" / consume("age").boolean(),
                response.any()),
            route(
                // TODO: these should be typed, and some should be required()
                POST bind "/headers"
                    and header("b").required() // boolean, required()
                    and header("s") // string
                    and header("i") // integer
                    and header("j") // json
                ,
                response.any()),
            route(
                POST bind "/body_string",
                response.text()),
            route(
                POST bind "/body_json_noschema"
                    but request.json(json),
                response.any()
            ),
            route(
                POST bind "/body_json_response",
                OK with induceSchema(json) {
                    obj("aNullField" to nullNode(),
                        "aNumberField" to number(123))
                }
            ),
            route(
                POST bind "/body_json_schema",
                OK with induceSchema(json, SchemaId("someDefinitionId")) {
                    obj("anAnotherObject" to obj(
                        "aNullField" to nullNode(),
                        "aNumberField" to number(123)))
                }
            )
        )
//            routes += "/body_json_list_schema" meta {
//                receiving(json.body("json").toLens() to json {
//                    array(obj("aNumberField" to number(123)))
//                })
//            } bindContract POST to { Response(OK) }
//            routes += "/basic_auth" meta {
//                security = BasicAuthSecurity("realm", credentials)
//            } bindContract POST to { Response(OK) }
//            routes += "/and_auth" meta {
//                security = BasicAuthSecurity("foo", credentials, "and1").and(BasicAuthSecurity("foo", credentials, "and2"))
//            } bindContract POST to { Response(OK) }
//            routes += "/or_auth" meta {
//                security = BasicAuthSecurity("foo", credentials, "or1").or(BasicAuthSecurity("foo", credentials, "or2"))
//            } bindContract POST to { Response(OK) }
//            routes += "/oauth2_auth" meta {
//                security = AuthCodeOAuthSecurity(OAuthProvider.gitHub({ Response(OK) },
//                    credentials,
//                    Uri.of("http://localhost/callback"),
//                    FakeOAuthPersistence()))
//            } bindContract POST to { Response(OK) }
//            routes += "/body_form" meta {
//                receiving(Body.webForm(Strict,
//                    FormField.boolean().required("b", "booleanField"),
//                    FormField.int().optional("i", "intField"),
//                    FormField.string().optional("s", "stringField"),
//                    json.lens(FormField).required("j", "jsonField")
//                ).toLens())
//            } bindContract POST to { Response(OK) }
//            routes += "/produces_and_consumes" meta {
//                produces += APPLICATION_JSON
//                produces += APPLICATION_XML
//                consumes += OCTET_STREAM
//                consumes += APPLICATION_FORM_URLENCODED
//            } bindContract GET to { Response(OK) }
//            routes += "/returning" meta {
//                returning("no way jose" to Response(FORBIDDEN).with(customBody of json { obj("aString" to string("a message of some kind")) }))
//            } bindContract POST to { Response(OK) }
//            routes += "/body_auto_schema" meta {
//                receiving(Body.auto<ArbObject2>().toLens() to ArbObject2(
//                    "s",
//                    ArbObject1(Foo.bar),
//                    listOf(1),
//                    true
//                ), "someOtherId")
//            } bindContract POST to { Response(OK) }
//            routes += "/body_auto_schema" meta {
//                receiving(Body.auto<ArbObject3>().toLens() to ArbObject3(Uri.of("http://foowang"), mapOf("foo" to 123)))
//                returning(status = CREATED, body = Body.auto<List<ArbObject1>>().toLens() to listOf(ArbObject1(Foo.bing)))
//            } bindContract PUT to { Response(OK) }
//            routes += "/body_auto_schema_multiple_response_schemas" meta {
//                returning(OK, Body.auto<ArbObject1>().toLens() to ArbObject1(Foo.bing))
//                returning(CREATED, Body.auto<ArbObject1>().toLens() to ArbObject1(Foo.bing))
//                returning(CREATED, Body.auto<ArbObject3>().toLens() to ArbObject3(Uri.of("http://foowang"), mapOf("foo" to 123)))
//            } bindContract POST to { Response(OK) }
//            routes += "/body_auto_schema_multiple_request_schemas" meta {
//                receiving(Body.auto<ArbObject1>().toLens() to ArbObject1(Foo.bing))
//                receiving(Body.auto<ArbObject3>().toLens() to ArbObject3(Uri.of("http://foowang"), mapOf("foo" to 123)))
//            } bindContract POST to { Response(OK) }
//            routes += "/body_auto_schema_name_definition_id" meta {
//                val toLens = Body.auto<InterfaceHolder>().toLens()
//                returning(OK, toLens to InterfaceHolder(Impl1()), definitionId = "impl1")
//                returning(OK, toLens to InterfaceHolder(Impl2()), definitionId = "impl2")
//            } bindContract POST to { Response(OK) }
//            routes += "/body_auto_map" meta {
//                receiving(Body.auto<Map<String, *>>().toLens() to mapOf("foo" to 123))
//            } bindContract PUT to { Response(OK) }
//            routes += "/multipart_fields" meta {
//                val field = MultipartFormField.required("stringField")
//                val pic = MultipartFormFile.required("fileField")
//                receiving(Body.multipartForm(Strict, field, pic).toLens())
//            } bindContract PUT to { Response(OK) }
//            routes += "/bearer_auth" meta {
//                security = BearerAuthSecurity("foo")
//            } bindContract POST to { Response(OK) }
//        }
//            routes += "/queries" meta {
//                queries += Query.boolean().required("b", "booleanQuery")
//                queries += Query.string().optional("s", "stringQuery")
//                queries += Query.int().optional("i", "intQuery")
//                queries += json.lens(Query).optional("j", "jsonQuery")
//            } bindContract POST to { Response(OK).body("hello") }
//            routes += "/cookies" meta {
//                cookies += Cookies.required("b", "requiredCookie")
//                cookies += Cookies.optional("s", "optionalCookie")
//            } bindContract POST to { Response(OK).body("hello") }

        println(document(routes))
    }

    private fun document(routes: List<OpenApiRoute<*, *>>): String {
        val api = api(routes)

        val renderer = V3Renderer(json, object : JsonRenderer<Any, JsonNode>, Json<JsonNode> by json {
            override fun render(value: Any): JsonNode =
                json.asJsonObject(value)
        })

        return json.asJsonString(renderer.render(api))
    }
}

