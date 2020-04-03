package org.http4k.typesafe.functional

/**
 * A lens is something that, for example, knows how to both extract a named
 * header from an http request, and how to set it. Or how to deserialize
 * a request body into an object, and how to serialize the same body and
 * put it into a request body.
 *
 * It's useful because the same lens can be used on the client side to
 * inject the header or body into the request, and on the server side to
 * read the values out.
 */
interface PolymorphicLens<Source, SourceAfterInjection, InjectedValue, ExtractedValue> {
    fun get(from: Source): ExtractedValue
    fun set(into: Source, value: InjectedValue): SourceAfterInjection

    fun invoke(into: Source, value: InjectedValue) = this.set(into, value)
    fun invoke(from: Source) = this.get(from)
}