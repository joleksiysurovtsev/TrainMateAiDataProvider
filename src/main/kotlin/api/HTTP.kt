package dev.surovtsev.api

import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*

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

fun Application.configureCORS() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowCredentials = true
    }
}