package org.http4k.openapi.builders

import org.http4k.openapi.Tag
import org.http4k.util.Appendable
import org.http4k.util.builders.BaseBuilder

class TagDsl(val original: Tag) : BaseBuilder<Tag, TagDsl>(::TagDsl) {
    var name = original.name
    var description = original.description
    var extensions = Appendable.of(original.extensions)

    override fun invoke(f: TagDsl.() -> Unit) = f(this)

    override fun build() = Tag(
        name,
        description,
        extensions.all
    )

}