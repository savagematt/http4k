package org.http4k.typesafe.functional


/**
 * See: https://arrow-kt.io/docs/0.10/patterns/glossary/#higher-kinds
 *
 * We use higher-kinded types to allow us to add arbitrary functionality
 * to `MessageLens`. The immediate example is that we want openapi support
 * to be layered on top of basic routing, rather than coupled in to it.
 *
 * Take a look at the [org.http4k.typesafe.routing.Routing]
 * [typeclass](https://arrow-kt.io/docs/0.10/patterns/glossary/#using-higher-kinds-with-typeclasses). We
 * can implement [org.http4k.typesafe.routing.Routing] for each extension
 * we create.
 *
 * We could have added `fun document(info: OpenApiInfo) : OpenApiInfo`
 * onto the base `MessageLens` interface. But then what happens if
 * somebody wants [raml](https://raml.org/) support? Do we add another
 * method? This gets messy quickly.
 *
 * It was a hard decision to use higher-kinded types. We are attempting to
 * make typesafe routing as approachable as possible. We want to introduce
 * the bare minimum concepts required to make it work, and we're copying
 * those across from arrow rather than depending on it.
 *
 * Arrow is beautiful, obviously, and very powerful. But for the teams
 * we work on, it is a step too far towards Haskell, and we want to
 * build a routing module people will find approachable enough to
 * be confident betting on.
 *
 * This may be the wrong decision for your taste or circumstance, and
 * we do understand that. It would be interesting to see a routing
 * layer over http4k using the full power of arrow, but this won't
 * be it.
 */
interface Kind2<F, A, B>

