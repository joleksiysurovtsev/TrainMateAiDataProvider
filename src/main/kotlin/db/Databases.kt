package dev.surovtsev.db

import dev.surovtsev.api.configureExerciseRoutes
import io.ktor.server.application.*
import dev.surovtsev.service.ExerciseService
import dev.surovtsev.service.MediaService
import java.sql.Connection
import java.sql.DriverManager
import org.flywaydb.core.Flyway

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

/**
 * Makes a connection to a Postgres database.
 *
 * In order to connect to your running Postgres process,
 * please specify the following parameters in your configuration file:
 * - postgres.url -- Url of your running database process.
 * - postgres.user -- Username for database connection
 * - postgres.password -- Password for database connection
 *
 * If you don't have a database process running yet, you may need to [download]((https://www.postgresql.org/download/))
 * and install Postgres and follow the instructions [here](https://postgresapp.com/).
 * Then, you would be able to edit your url,  which is usually "jdbc:postgresql://host:port/database", as well as
 * user and password values.
 *
 * @return [Connection] that represent connection to the database. Please, don't forget to close this connection when
 * your application shuts down by calling [Connection.close]
 * */
fun Application.connectToPostgres(): Connection {
    Class.forName("org.postgresql.Driver")

    val url = environment.config.property("postgres.url").getString()
    log.info("Connecting to postgres database at $url")
    val user = environment.config.property("postgres.user").getString()
    val password = environment.config.property("postgres.password").getString()

    return DriverManager.getConnection(url, user, password)
}
