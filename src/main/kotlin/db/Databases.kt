package dev.surovtsev.db

import dev.surovtsev.api.configureExerciseRoutes
import io.ktor.server.application.*
import dev.surovtsev.service.ExerciseService
import dev.surovtsev.service.MediaService
import java.sql.Connection
import java.sql.DriverManager
import org.flywaydb.core.Flyway
import java.net.URI

fun Application.configureDatabases() {
    val dbConnection: Connection = connectToPostgres()

    // Run Flyway migrations
    runFlywayMigration(
        environment.config.property("postgres.url").getString(),
        environment.config.property("postgres.user").getString(),
        environment.config.property("postgres.password").getString()
    )

    // Initialize services
    val exerciseService = ExerciseService(dbConnection)
    val mediaService = MediaService(dbConnection)

    // Configure routes
    configureExerciseRoutes(exerciseService, mediaService)
}

/**
 * Runs Flyway database migrations.
 */
fun runFlywayMigration(url: String, user: String, password: String) {
    val flyway = Flyway.configure()
        .dataSource(url, user, password)
        .load()

    flyway.migrate()
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

    log.info("Connecting to DB with jdbc URL: $url")

    return DriverManager.getConnection(url, user, password)
}
