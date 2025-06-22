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
    val host = System.getenv("PGHOST") ?: "localhost"
    val port = System.getenv("PGPORT") ?: "5432"
    val db = System.getenv("PGDATABASE") ?: "railway"
    val user = System.getenv("POSTGRES_USER") ?: "postgres"
    val password = System.getenv("POSTGRES_PASSWORD") ?: "password"
    val defaultUrl = "jdbc:postgresql://$host:$port/$db"
    val databaseUrl = System.getenv("DATABASE_URL") ?: defaultUrl



    Class.forName("org.postgresql.Driver")
    return DriverManager.getConnection(databaseUrl, user, password)
}