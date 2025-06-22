package dev.surovtsev.enums

import kotlinx.serialization.Serializable

@Serializable
enum class MediaKind {
    IMAGE,
    VIDEO,
    GIF,
    SITE
}