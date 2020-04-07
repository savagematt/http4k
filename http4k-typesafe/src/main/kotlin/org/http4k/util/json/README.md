# What?

We want typesafe routing to be a standalone module, so users don't need to
pull in [http4k-contract](../../../../../../../../http4k-contract).

These classes were the minimum required to reproduce the existing json
marshalling functionality in `http4k-contract`.

We could have extracted them to a common shared module used by -contract and
-typesafe, but we'd end up with an awful lot of modules if we kept doing that. 
Perhaps we'll do it later.