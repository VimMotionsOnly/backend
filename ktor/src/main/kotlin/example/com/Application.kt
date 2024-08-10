package example.com

import example.com.models.EditorGame
import example.com.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val game = EditorGame()
    configureSerialization()
    configureSockets()
//    configureSecurity()
    configureRouting(game)
}
