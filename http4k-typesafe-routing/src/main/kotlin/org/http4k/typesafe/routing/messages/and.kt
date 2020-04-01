package org.http4k.typesafe.routing.messages

import org.http4k.core.HttpMessage
import org.http4k.typesafe.routing.MessageLens

infix fun <M : HttpMessage, A, B> MessageLens<M, A>.and(lens: MessageLens<M, B>) = pair(this, lens)