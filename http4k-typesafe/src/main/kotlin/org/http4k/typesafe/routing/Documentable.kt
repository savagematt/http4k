package org.http4k.typesafe.routing

/**
 * Something that is capable of describing itself in some sort of
 * document
 */
interface Documentable<T> {
    fun document(doc: T): T
}

fun <D> fold(vararg documentables: Documentable<D>): (D) -> D =
    { it: D ->
        fold(it, *documentables)
    }

fun <D> fold(document: D, vararg documentables: Documentable<D>): D =
    documentables.fold(document)
    { doc, documentable ->
        documentable.document(doc)
    }