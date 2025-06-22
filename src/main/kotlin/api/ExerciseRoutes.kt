package dev.surovtsev.api

import dev.surovtsev.data.ExerciseDto
import dev.surovtsev.data.MediaDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import dev.surovtsev.service.ExerciseService
import dev.surovtsev.service.MediaService

fun Application.configureExerciseRoutes(exerciseService: ExerciseService, mediaService: MediaService) {
    routing {
        route("/api") {
            // Exercise routes
            route("/exercises") {
                // Get all exercises
                get {
                    val exercises = exerciseService.getAll()
                    call.respond(exercises)
                }

                // Create a new exercise
                post {
                    val exercise = call.receive<ExerciseDto>()
                    val id = exerciseService.create(exercise)
                    call.respond(HttpStatusCode.Created, mapOf("id" to id))
                }

                // Get exercise by ID
                get("/{id}") {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Invalid ID")
                    try {
                        val exercise = exerciseService.read(id)
                        call.respond(exercise)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                    }
                }

                // Update exercise
                put("/{id}") {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Invalid ID")
                    val exercise = call.receive<ExerciseDto>()
                    try {
                        exerciseService.update(id, exercise)
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Exercise updated successfully"))
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                    }
                }

                // Delete exercise
                delete("/{id}") {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Invalid ID")
                    try {
                        exerciseService.delete(id)
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Exercise deleted successfully"))
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                    }
                }

                // Get all media for an exercise
                get("/{id}/media") {
                    val exerciseId = call.parameters["id"] ?: throw IllegalArgumentException("Invalid exercise ID")
                    try {
                        val mediaList = mediaService.getByExerciseId(exerciseId)
                        call.respond(mediaList)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                    }
                }
            }

            // Media routes
            route("/media") {
                // Create a new media
                post {
                    val media = call.receive<MediaDto>()
                    val id = mediaService.create(media)
                    call.respond(HttpStatusCode.Created, mapOf("id" to id))
                }

                // Get media by ID
                get("/{id}") {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Invalid ID")
                    try {
                        val media = mediaService.read(id)
                        call.respond(media)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                    }
                }

                // Update media
                put("/{id}") {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Invalid ID")
                    val media = call.receive<MediaDto>()
                    try {
                        mediaService.update(id, media)
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Media updated successfully"))
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                    }
                }

                // Delete media
                delete("/{id}") {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Invalid ID")
                    try {
                        mediaService.delete(id)
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Media deleted successfully"))
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                    }
                }
            }
        }
    }
}