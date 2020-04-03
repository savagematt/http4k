#!/bin/bash

source ./release-functions.sh

ensure_release_commit

maven_publish "http4k-format-jackson"
maven_publish "http4k-format-gson"
maven_publish "http4k-format-moshi"
maven_publish "http4k-format-argo"

maven_publish "http4k-testing-approval"
maven_publish "http4k-testing-chaos"
maven_publish "http4k-testing-hamkrest"
maven_publish "http4k-testing-servirtium"
maven_publish "http4k-testing-webdriver"

maven_publish "http4k-typesafe"
maven_publish "http4k-typesafe-openapi"
maven_publish "http4k-typesafe-simple"