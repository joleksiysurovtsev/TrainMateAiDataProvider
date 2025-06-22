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
    val host = System.getenv("PGHOST") ?: "localhost"
    val port = System.getenv("PGPORT") ?: "5432"
    val db = System.getenv("PGDATABASE") ?: "railway"
    val user = System.getenv("PGUSER") ?: "postgres"
    val password = System.getenv("PGPASSWORD") ?: "password"

    val url = "jdbc:postgresql://$host:$port/$db"

    log.info("Connecting to DB: $url as user=$user")

    Class.forName("org.postgresql.Driver")
    return DriverManager.getConnection(url, user, password)
}