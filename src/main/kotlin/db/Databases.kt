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
    val (connection, user, password) = connectToPostgresWithCreds()

    runFlywayMigration(connection.metaData.url, user, password)

    val exerciseService = ExerciseService(connection)
    val mediaService = MediaService(connection)

    configureExerciseRoutes(exerciseService, mediaService)
}

fun runFlywayMigration(url: String, user: String, password: String) {
    Flyway.configure()
        .dataSource(url, user, password)
        .load()
        .migrate()
}

fun Application.connectToPostgresWithCreds(): Triple<Connection, String, String> {
    val databaseUrl = System.getenv("DATABASE_URL")

    val (url, user, password) = if (databaseUrl != null && !databaseUrl.startsWith("jdbc:")) {
        val uri = URI(databaseUrl.replace("postgres://", "postgresql://"))
        val userInfo = uri.userInfo?.split(":")
            ?: error("DATABASE_URL must contain user credentials")

        val user = userInfo[0]
        val password = userInfo.getOrNull(1) ?: ""
        val url = "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}"
        Triple(url, user, password)
    } else {
        val host = System.getenv("PGHOST") ?: "localhost"
        val port = System.getenv("PGPORT") ?: "5432"
        val db = System.getenv("PGDATABASE") ?: "trainmate_ai_data"
        val user = System.getenv("POSTGRES_USER") ?: "postgres"
        val password = System.getenv("POSTGRES_PASSWORD") ?: "postgres"
        val url = databaseUrl ?: "jdbc:postgresql://$host:$port/$db"
        Triple(url, user, password)
    }

    log.info("🔌 Connecting to DB at $url as user '$user'")
    Class.forName("org.postgresql.Driver")
    val connection = DriverManager.getConnection(url, user, password)
    return Triple(connection, user, password)
}