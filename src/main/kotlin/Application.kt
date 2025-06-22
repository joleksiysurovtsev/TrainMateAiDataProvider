package dev.surovtsev

import dev.surovtsev.api.configureHTTP
import dev.surovtsev.api.configureRouting
import dev.surovtsev.db.configureDatabases
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
