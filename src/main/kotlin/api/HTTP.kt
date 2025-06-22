package dev.surovtsev.api

import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {
    routing {
        // Serve OpenAPI specification
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
    }
    routing {
        // Serve Swagger UI
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}
