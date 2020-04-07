# http4k-openapi

Makes open api easily programmable.

## Immutable data classes 

We have build data classes for all open api 3 concepts in:

https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md

## Mutable builders

We use the 
[lambdas with receivers pattern](https://medium.com/tompee/idiomatic-kotlin-lambdas-with-receiver-and-dsl-3cd3348e1235) 
to create a 
[readable dsl](src/main/kotlin/org/http4k/openapi/builders)
for mutating open api documents.

## Json rendering

[V3Renderer](src/main/kotlin/org/http4k/openapi/V3Renderer.kt) 
will render an open api document into json.

It uses http4k's 
[Json](../http4k-core/src/main/kotlin/org/http4k/format/Json.kt)
abstraction to be compatible with your json engine of choice.

## Composition of openapi documents

This functionality was driven from requirements in [typesafe routing](../http4k-typesafe-openapi),
where we want to combine the openapi docs from two lenses we are merging together.

If I have two openapi documents I want to combine into a single api, there may be conflicts
between them that need to be resolved. The same route may appear in each, with different
security, as an example.

Conflicts can be resolved using "and" or "or" behaviour.

"Or" merging will for example add an alternative security method to an operation, where the
2 source documents contain different security requirements for the same operation.

Whereas "and" merging would require _both_ security mechanisms on the operation.

Primitive values like `description` will be merged by providing a reference "empty" or "null" 
document, allowing the merger to choose the value from whichever of our two documents changed 
`description` from the value in the reference document, with one of the two source documents
winning if they have both changed the field.