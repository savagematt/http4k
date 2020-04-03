package org.http4k.typesafe.functional


/**
 * See: https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds
 *
 * The easiest way to think about higher-kinded types is that:
 *
 * `Kind2<F, A, B>` is to `SimpleLens<A, B>`
 *
 * as
 *
 * `(a: String, b: Int) -> String` is to `{ a: String, b: Int -> "$a$b" }`
 *
 * `SimpleLens<A, B>` is an example of `Kind2<F, A, B>`, as is `Map<A, B>`.
 *
 * We use higher-kinded types to allow us to add arbitrary functionality
 * to `MessageLens`. The immediate example is that we want openapi support
 * to be layered on top of basic routing, rather than coupled in to it. We
 * don't want [org.http4k.typesafe.routing.MessageLens] to know anything
 * about openapi.
 *
 * Take a look at the [org.http4k.typesafe.routing.Routing]
 * [type class](https://arrow-kt.io/docs/0.10/patterns/glossary/#using-higher-kinds-with-typeclasses).
 *
 * And then take a look at an example of an implementation, such as
 * [org.http4k.typesafe.openapi.OpenApiRouting]. You'll notice the
 * implementation only concerns itself with openapi-related logic.
 *
 * We could have added `fun document(info: OpenApiInfo) : OpenApiInfo`
 * onto the base `MessageLens` interface. But then what happens if
 * somebody wants [raml](https://raml.org/) support? Do we add another
 * method? This gets messy quickly. And what if I don't care about openapi?
 * Now if I create my own lens I still need to add openapi functionality,
 * and then raml, and then...
 *
 * It was a hard decision to use higher-kinded types. We are attempting to
 * make typesafe routing as approachable as possible. We want to introduce
 * the bare minimum of concepts required to make it work. But a type class
 * is obviously the correct way to support a standard set of operations
 * over a bunch of different implementations of lenses.
 *
 *
 * We're reimplementing functional concepts rather than depending on arrow.
 *
 * Arrow is beautiful, obviously, and very powerful. But for the teams
 * we work on, it is a step too far towards Haskell, and we want to
 * build a routing module people will find approachable enough to
 * be confident betting on. We also don't want to be beholden to breaking
 * changes in arrow. Nor do we want to force a choice of arrow version
 * on to users.
 *
 * Not reusing arrow may be the wrong decision for your taste or
 * circumstance, and we do understand that. It would be interesting to see
 * a routing layer over http4k using the full power of arrow, but this
 * won't be it.
 */
interface Kind<F, A>

interface Kind2<F, A, B>
interface Kind3<F, A, B, C>
interface Kind4<F_, A, B, C, D>
interface Kind5<F_, A, B, C, D, E>
interface Kind6<F_, A, B, C, D, E, F>
interface Kind7<F_, A, B, C, D, E, F, G>
interface Kind8<F_, A, B, C, D, E, F, G, H>
interface Kind9<F_, A, B, C, D, E, F, G, H, I>
interface Kind10<F_, A, B, C, D, E, F, G, H, I, J>

