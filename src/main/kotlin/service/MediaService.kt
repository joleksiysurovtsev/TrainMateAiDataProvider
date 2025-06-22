package dev.surovtsev.service

import dev.surovtsev.data.MediaDto
import dev.surovtsev.enums.MediaKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection

class MediaService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_MEDIA = """
            CREATE TABLE IF NOT EXISTS media (
                id VARCHAR(255) PRIMARY KEY,
                exercise_id VARCHAR(255) NOT NULL,
                kind VARCHAR(20) NOT NULL,
                url VARCHAR(255) NOT NULL,
                "order" INT NOT NULL DEFAULT 0,
                FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE
            );
        """

        private const val SELECT_MEDIA_BY_ID = """
            SELECT id, exercise_id, kind, url, "order" 
            FROM media 
            WHERE id = ?
        """

        private const val SELECT_MEDIA_BY_EXERCISE_ID = """
            SELECT id, exercise_id, kind, url, "order" 
            FROM media 
            WHERE exercise_id = ?
            ORDER BY "order"
        """

        private const val INSERT_MEDIA = """
            INSERT INTO media (id, exercise_id, kind, url, "order") 
            VALUES (?, ?, ?, ?, ?)
        """

        private const val UPDATE_MEDIA = """
            UPDATE media 
            SET exercise_id = ?, kind = ?, url = ?, "order" = ? 
            WHERE id = ?
        """

        private const val DELETE_MEDIA = "DELETE FROM media WHERE id = ?"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_MEDIA)
    }

    // Create new media
    suspend fun create(media: MediaDto): String = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_MEDIA)
        statement.setString(1, media.id)
        statement.setString(2, media.exerciseId)
        statement.setString(3, media.kind.name)
        statement.setString(4, media.url)
        statement.setInt(5, media.order)
        statement.executeUpdate()
        return@withContext media.id
    }

    // Read a media
    suspend fun read(id: String): MediaDto = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_MEDIA_BY_ID)
        statement.setString(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val exerciseId = resultSet.getString("exercise_id")
            val kindName = resultSet.getString("kind")
            val kind = MediaKind.valueOf(kindName)
            val url = resultSet.getString("url")
            val order = resultSet.getInt("order")
            return@withContext MediaDto(id, exerciseId, kind, url, order)
        } else {
            throw Exception("Media not found")
        }
    }

    // Get all media for an exercise
    suspend fun getByExerciseId(exerciseId: String): List<MediaDto> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_MEDIA_BY_EXERCISE_ID)
        statement.setString(1, exerciseId)
        val resultSet = statement.executeQuery()
        val mediaList = mutableListOf<MediaDto>()

        while (resultSet.next()) {
            val id = resultSet.getString("id")
            val kindName = resultSet.getString("kind")
            val kind = MediaKind.valueOf(kindName)
            val url = resultSet.getString("url")
            val order = resultSet.getInt("order")
            mediaList.add(MediaDto(id, exerciseId, kind, url, order))
        }
        return@withContext mediaList
    }

    // Update a media
    suspend fun update(id: String, media: MediaDto) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_MEDIA)
        statement.setString(1, media.exerciseId)
        statement.setString(2, media.kind.name)
        statement.setString(3, media.url)
        statement.setInt(4, media.order)
        statement.setString(5, id)
        statement.executeUpdate()
    }

    // Delete a media
    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_MEDIA)
        statement.setString(1, id)
        statement.executeUpdate()
    }
}