package org.http4k.util.functional

import com.natpryce.Result

/**
 * A `ResultLens` uses a `Result` to report back any errors
 * in injecting or extracting values.
 */
interface ResultLens<Source, Value, E> : PolymorphicLens<
    Source, Result<Source, E>,
    Value, Result<Value, E>>