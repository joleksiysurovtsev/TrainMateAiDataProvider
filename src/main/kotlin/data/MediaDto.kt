package dev.surovtsev.data

import dev.surovtsev.enums.MediaKind
import kotlinx.serialization.Serializable

@Serializable
data class MediaDto(
    val id: String,
    val exerciseId: String,
    val kind: MediaKind,
    val url: String,
    val order: Int = 0
)