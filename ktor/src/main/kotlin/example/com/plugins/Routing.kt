package example.com.plugins

import example.com.models.EditorGame
import example.com.socket
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(game: EditorGame) {
    routing {
        socket(game)
    }
}
