package dev.surovtsev.db

import dev.surovtsev.api.configureExerciseRoutes
import dev.surovtsev.service.ExerciseService
import dev.surovtsev.service.MediaService
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import java.net.URI
import java.sql.Connection
import java.sql.DriverManager

fun Application.configureDatabases() {
    val dbConnection = connectToPostgres()

    runFlywayMigration(dbConnection)

    val exerciseService = ExerciseService(dbConnection)
    val mediaService = MediaService(dbConnection)

    configureExerciseRoutes(exerciseService, mediaService)
}

fun runFlywayMigration(connection: Connection) {
    val meta = connection.metaData
    val url = meta.url
    val user = meta.userName

    Flyway.configure()
        .dataSource(url, user, null)
        .load()
        .migrate()
}

fun Application.connectToPostgres(): Connection {
    val rawUrl = System.getenv("DATABASE_URL")
        ?: error("DATABASE_URL is not set")

    val uri = URI(rawUrl.replace("postgres://", "postgresql://"))

    val userInfo = uri.userInfo?.split(":")
        ?: error("Missing user credentials in DATABASE_URL")

    val user = userInfo[0]
    val password = userInfo.getOrNull(1) ?: ""
    val url = "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}"

    log.info("Connecting to Postgres: $url (user=$user)")

    Class.forName("org.postgresql.Driver")
    return DriverManager.getConnection(url, user, password)
}