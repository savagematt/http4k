package org.http4k.typesafe.openapi

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.oneeyedmen.okeydoke.Approver
import com.oneeyedmen.okeydoke.junit5.KotlinApprovalsExtension
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.customise
import org.http4k.openapi.SchemaId
import org.http4k.openapi.Tag
import org.http4k.typesafe.openapi.documentable.description
import org.http4k.typesafe.openapi.documentable.meta
import org.http4k.typesafe.openapi.routing.and
import org.http4k.typesafe.openapi.routing.basicAuthServer
import org.http4k.typesafe.openapi.routing.at
import org.http4k.typesafe.openapi.routing.boolean
import org.http4k.typesafe.openapi.routing.consume
import org.http4k.typesafe.openapi.routing.div
import org.http4k.typesafe.openapi.routing.request
import org.http4k.typesafe.openapi.routing.request.header
import org.http4k.typesafe.openapi.routing.required
import org.http4k.typesafe.openapi.routing.response
import org.http4k.typesafe.openapi.routing.with
import org.http4k.typesafe.routing.Route
import org.http4k.typesafe.routing.security.basicAuthValidator
import org.http4k.util.data.Tuple2
import org.http4k.util.data.Tuple4
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

private val json: ConfigurableJackson = ConfigurableJackson(KotlinModule().asConfigurable().customise())

enum class Foo {
    bar, bing
}

data class ArbObject1(val anotherString: Foo)
data class ArbObject2(val string: String, val child: ArbObject1?, val numbers: List<Int>, val bool: Boolean)
data class ArbObject3(val uri: Uri, val additional: Map<String, *>)

private object Routes {
    val nometa: OpenApiRoute<Unit, Unit> = Route(GET at "/nometa",
        response.any()
    )
    val descriptions: OpenApiRoute<Unit, Unit> = Route(GET at "/descriptions" meta {
        summary = "endpoint"
        description = "some rambling description of what this thing actually does"
        tags += Tag("tag3")
        tags += Tag("tag1")
        deprecated = true
    },
        response.any()
    )

    val paths: OpenApiRoute<Tuple2<String, Boolean>, Unit> = Route(POST at "/paths" / consume("firstName") / "bertrand" / consume("age").boolean(),
        response.any())

    val headers: OpenApiRoute<Tuple4<String, String?, String?, String?>, Unit> =
        Route(// TODO: these should be typed
            POST at "/headers"
                and header("b").required() // boolean, required()
                and header("s") // string
                and header("i") // integer
                and header("j"), // json
            response.any())

    val bodyString: OpenApiRoute<Unit, String> = Route(POST at "/body_string",
        response.text())

    val jsonNoSchema: OpenApiRoute<JsonNode, Unit> = Route(POST at "/body_json_noschema"
        and request.json(json),
        response.any()
    )

    val jsonResponse: OpenApiRoute<Unit, JsonNode> = Route(POST at "/body_json_response",
        OK with json.plain {
            obj("aNullField" to nullNode(),
                "aNumberField" to number(123))
        }
    )
    val jsonWithSchema: OpenApiRoute<Unit, JsonNode> = Route(POST at "/body_json_schema",
        OK with json.plain(
            SchemaId("someDefinitionId")) {
            obj("anAnotherObject" to obj(
                "aNullField" to nullNode(),
                "aNumberField" to number(123)))
        }
    )
    val jsonWithListSchema: OpenApiRoute<Unit, JsonNode> = Route(POST at "/body_json_list_schema",
        OK with json.plain {
            array(listOf(obj("aNumberField" to number(123))))
        }
    )

    val basicAuth: OpenApiRoute<String, Unit> = Route(POST at "/basic_auth" and
        basicAuthServer(
            basicAuthValidator("realm") {
                it.password == "password"
            }),
        response.any())

    val autoSchemaWithId: OpenApiRoute<ArbObject2, Unit> = Route(POST at "/body_auto_schema" and
        json.typed(SchemaId("someOtherId"),
            ArbObject2(
                "s",
                ArbObject1(Foo.bar),
                listOf(1),
                true
            )),
        OK with response.any())

    val autoSchemaWithList: OpenApiRoute<ArbObject3, List<ArbObject1>> = Route(PUT at "/body_auto_schema" and
        json.typed(ArbObject3(
            Uri.of("http://foowang"),
            mapOf("foo" to 123))),
        CREATED with json.typed(
            listOf(ArbObject1(Foo.bing))))

    val responseStatus: OpenApiRoute<Unit, JsonNode> = Route(POST at "/returning",
        FORBIDDEN with json
            .plain<Response, JsonNode> {
                obj("aString" to string("a message of some kind"))
            }.description("no way jose"))


//            route(
//                POST bind "/or_auth" but
//                    basicAuthServer(
//                        basicAuthValidator("realm") {
//                            it.password == "password"
//                        }),
//                FORBIDDEN with json
//                    .plain<Response, JsonNode> {
//                        obj("aString" to string("a message of some kind"))
//                    }.description("no way jose"))

//            routes += "/or_auth" meta {
//                security = BasicAuthSecurity("foo", credentials, "or1").or(BasicAuthSecurity("foo", credentials, "or2"))
//            } bindContract POST to { Response(OK) }

    //            routes += "/produces_and_consumes" meta {
//                produces += APPLICATION_JSON
//                produces += APPLICATION_XML
//                consumes += OCTET_STREAM
//                consumes += APPLICATION_FORM_URLENCODED
//            } bindContract GET to { Response(OK) }
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
//            routes += "/and_auth" meta {
//                security = BasicAuthSecurity("foo", credentials, "and1").and(BasicAuthSecurity("foo", credentials, "and2"))
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
}

@ExtendWith(KotlinApprovalsExtension::class)
class OpenApiRenderingTest {

    @Test
    fun `produces sensible output for examples in ContractRendererContract`(approver: Approver) {

        val routes: List<OpenApiRoute<*, *>> = listOf(
            Routes.nometa,
            Routes.descriptions,
            Routes.paths,
            Routes.headers,
            Routes.bodyString,
            Routes.jsonNoSchema,
            Routes.jsonResponse,
            Routes.jsonWithSchema,
            Routes.jsonWithListSchema,
            Routes.basicAuth,
            Routes.autoSchemaWithId,
            Routes.autoSchemaWithList,
            Routes.responseStatus)

        approver.assertApproved(render(routes))
    }
}