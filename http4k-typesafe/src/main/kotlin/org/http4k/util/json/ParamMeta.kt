package org.http4k.util.json

import org.http4k.lens.ParamMeta


val ParamMeta.value
    get() = when (this) {
        ParamMeta.ArrayParam -> "array"
        ParamMeta.StringParam -> "string"
        ParamMeta.ObjectParam -> "object"
        ParamMeta.BooleanParam -> "boolean"
        ParamMeta.IntegerParam -> "integer"
        ParamMeta.FileParam -> "string"
        ParamMeta.NumberParam -> "number"
        ParamMeta.NullParam -> "null"
    }