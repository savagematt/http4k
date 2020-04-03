package org.http4k.typesafe.routing.messages

import com.natpryce.flatMap
import com.natpryce.map
import org.http4k.core.HttpMessage
import org.http4k.typesafe.data.Tuple10
import org.http4k.typesafe.data.tuple
import org.http4k.typesafe.routing.MessageLens
import org.http4k.typesafe.routing.simple.SimpleLens

class Tuple10Lens<M : HttpMessage, A, B, C,D,E,F,G,H,I,J>(
    val a: MessageLens<M, A>,
    val b: MessageLens<M, B>,
    val c: MessageLens<M, C>,
    val d: MessageLens<M, D>,
    val e: MessageLens<M, E>,
    val f: MessageLens<M, F>,
    val g: MessageLens<M, G>,
    val h: MessageLens<M, H>,
    val i: MessageLens<M, I>,
    val j: MessageLens<M, J>
) : SimpleLens<M, Tuple10<A, B, C, D, E, F, G, H, I, J>> {
    override fun get(from: M) =
        a.get(from).flatMap { a ->
            b.get(from).flatMap { b ->
                c.get(from).flatMap { c ->
                    d.get(from).flatMap { d ->
                        e.get(from).flatMap { e ->
                            f.get(from).flatMap { f ->
                                g.get(from).flatMap { g ->
                                    h.get(from).flatMap { h ->
                                        i.get(from).flatMap { i ->
                                            j.get(from).map { j ->
                                                tuple(a,b,c,d,e,f,g,h,i,j)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    override fun set(into: M, value: Tuple10<A, B, C, D, E, F, G, H, I, J>) =
        a.set(into, value.a).flatMap {
            b.set(it, value.b).flatMap {
                c.set(it, value.c).flatMap {
                    d.set(it, value.d).flatMap {
                        e.set(it, value.e).flatMap {
                            f.set(it, value.f).flatMap {
                                g.set(it, value.g).flatMap {
                                    h.set(it, value.h).flatMap {
                                        i.set(it, value.i).flatMap {
                                            j.set(it, value.j)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

}