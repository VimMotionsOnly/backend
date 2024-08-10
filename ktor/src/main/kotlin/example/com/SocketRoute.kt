package example.com

import example.com.models.EditorGame
import example.com.models.GameState
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


fun Route.socket(game: EditorGame) {
    route("/document-editor"){
        webSocket {
            val player = game.connectPlayer(this)
            if(player != null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, " :D"))
                return@webSocket
            }
            try {
                incoming.consumeEach { frame ->
                    if(frame is Frame.Text){
                        val action = extractAction(frame.readText())
                        game.updateEditor(action.toString())
                        game.updatePlayerPosition(player.toString(), action.editor.length)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                game.disconnectPlayer(player.toString())
            }
        }
    }
}

private fun extractAction (message: String): GameState {

    val type = message.substringBefore("#")
    val body = message.substringAfter("#")

    return if(type == "editor_update"){
        Json.decodeFromString(body)
    } else {
        return GameState()
    }
}
