package dev.surovtsev.db

import dev.surovtsev.api.configureExerciseRoutes
import dev.surovtsev.service.ExerciseService
import dev.surovtsev.service.MediaService
import io.ktor.server.application.*
import io.ktor.server.util.url
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
    val databaseUrl = System.getenv("DATABASE_URL")

    val (url, user, password) = if (databaseUrl != null && !databaseUrl.startsWith("jdbc:")) {
        // Преобразуем PostgreSQL URI → JDBC URL
        val uri = URI(databaseUrl.replace("postgres://", "postgresql://"))
        val userInfo = uri.userInfo?.split(":")
            ?: error("DATABASE_URL must contain user credentials")

        val user = userInfo[0]
        val password = userInfo.getOrNull(1) ?: ""
        val url = "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}"
        Triple(url, user, password)
    } else {
        // Используем переменные среды напрямую
        val host = System.getenv("PGHOST") ?: "localhost"
        val port = System.getenv("PGPORT") ?: "5432"
        val db = System.getenv("PGDATABASE") ?: "railway"
        val user = System.getenv("POSTGRES_USER") ?: "postgres"
        val password = System.getenv("POSTGRES_PASSWORD") ?: "password"
        val url = databaseUrl ?: "jdbc:postgresql://$host:$port/$db"
        Triple(url, user, password)
    }

    log.info("🔌 Connecting to DB at $url as user '$user'")
    Class.forName("org.postgresql.Driver")
    return DriverManager.getConnection(url, user, password)
}