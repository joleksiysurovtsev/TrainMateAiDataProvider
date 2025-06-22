package dev.surovtsev.data

import dev.surovtsev.enums.ExerciseCategory
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseDto(
    val id: String,
    val name: String,
    val description: String,
    val category: ExerciseCategory,
)