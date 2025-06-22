package dev.surovtsev.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ExerciseCategory(
    val displayName: String,
    val parent: ExerciseCategory? = null
) {
    /* ----- basic ----- */
    Chest(displayName = "Chest"),
    Back(displayName = "Back"),
    Arms(displayName = "Arms"),
    Legs(displayName = "Legs"),
    Core(displayName = "Core"),
    Cardio(displayName = "Cardio"),
    All(displayName = "All"),

    /* ----- Arms + sub-categories ----- */
    Shoulders(displayName = "Shoulders", parent = Arms),
    Biceps(displayName = "Biceps", parent = Arms),
    Triceps(displayName = "Triceps", parent = Arms),
    Forearms(displayName = "Forearms", parent = Arms),

    /* ----- Legs + sub-categories ----- */
    Quads(displayName = "Quads", parent = Legs),
    Hamstrings(displayName = "Hamstrings", parent = Legs),
    Calves(displayName = "Calves", parent = Legs),
    Glutes(displayName = "Glutes", parent = Legs);

    /* ---------- helpers ---------- */

    /** Is the category "upper level". */
    val isRoot: Boolean get() = parent == null

    /** Daughter categories of current (we count lazily through values ()). */
    val children: List<ExerciseCategory> by lazy {
        entries.filter { it.parent == this }
    }

    companion object {
        /** All root categories (Chest, Back, Arms, Legs ...). */
        val ROOTS: List<ExerciseCategory> by lazy { entries.filter { it.isRoot } }

        /** Quick search on displayName - need Saver'u on screen. */
        val BY_NAME: Map<String, ExerciseCategory> by lazy {
            entries.associateBy { it.displayName }
        }
    }
}