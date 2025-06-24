package dev.surovtsev.service

import dev.surovtsev.data.ExerciseDto
import dev.surovtsev.enums.ExerciseCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection

class ExerciseService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_EXERCISES = """
            CREATE TABLE IF NOT EXISTS exercises (
                id VARCHAR(255) PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                description TEXT NOT NULL,
                category VARCHAR(50) NOT NULL,
                imageUrl VARCHAR(255) NOT NULL
            );
        """

        private const val SELECT_EXERCISE_BY_ID = """
            SELECT id, name, description, category, imageUrl
            FROM exercises 
            WHERE id = ?
        """

        private const val SELECT_ALL_EXERCISES = """
            SELECT id, name, description, category, imageUrl
            FROM exercises
        """

        private const val INSERT_EXERCISE = """
            INSERT INTO exercises (id, name, description, category, imageUrl) 
            VALUES (?, ?, ?, ?, ?)
        """

        private const val UPDATE_EXERCISE = """
            UPDATE exercises 
            SET name = ?, description = ?, category = ?, imageUrl = ?
            WHERE id = ?
        """

        private const val DELETE_EXERCISE = "DELETE FROM exercises WHERE id = ?"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_EXERCISES)
    }

    // Create new exercise
    suspend fun create(exercise: ExerciseDto): String = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_EXERCISE)
        statement.setString(1, exercise.id)
        statement.setString(2, exercise.name)
        statement.setString(3, exercise.description)
        statement.setString(4, exercise.category.name)
        statement.setString(5, exercise.imageUrl)
        statement.executeUpdate()
        return@withContext exercise.id
    }

    // Read an exercise
    suspend fun read(id: String): ExerciseDto = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_EXERCISE_BY_ID)
        statement.setString(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val name = resultSet.getString("name")
            val description = resultSet.getString("description")
            val categoryName = resultSet.getString("category")
            val category = ExerciseCategory.valueOf(categoryName)
            val imageUrl = resultSet.getString("imageUrl")
            return@withContext ExerciseDto(id, name, description, category, imageUrl)
        } else {
            throw Exception("Exercise not found")
        }
    }

    // Get all exercises
    suspend fun getAll(): List<ExerciseDto> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_ALL_EXERCISES)
        val resultSet = statement.executeQuery()
        val exercises = mutableListOf<ExerciseDto>()

        while (resultSet.next()) {
            val id = resultSet.getString("id")
            val name = resultSet.getString("name")
            val description = resultSet.getString("description")
            val categoryName = resultSet.getString("category")
            val category = ExerciseCategory.valueOf(categoryName)
            val imageUrl = resultSet.getString("imageUrl")
            exercises.add(ExerciseDto(id, name, description, category, imageUrl))
        }
        return@withContext exercises
    }

    // Update an exercise
    suspend fun update(id: String, exercise: ExerciseDto) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_EXERCISE)
        statement.setString(1, exercise.name)
        statement.setString(2, exercise.description)
        statement.setString(3, exercise.category.name)
        statement.setString(4, exercise.imageUrl)
        statement.setString(6, id)
        statement.executeUpdate()
    }

    // Delete an exercise
    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_EXERCISE)
        statement.setString(1, id)
        statement.executeUpdate()
    }
}