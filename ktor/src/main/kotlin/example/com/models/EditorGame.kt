package example.com.models

import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentHashMap

class EditorGame {

    private val state = MutableStateFlow(GameState())

    private val playerSockets = ConcurrentHashMap<String, WebSocketSession>()
    private val playerPositions = ConcurrentHashMap<String, Int>()
    private val gameScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        state.onEach(::broadcast).launchIn(gameScope)
    }

    fun connectPlayer(session: WebSocketSession): String? {
        val player = "test" // TODO: Replace with actual player identification logic
        state.update {
            if (state.value.connectedPlayers!!.contains(player)) {
                return null
            }
            if (!playerSockets.containsKey(player)) {
                playerSockets[player] = session
                playerPositions[player] = 0
            }

            it.copy(
                connectedPlayers = it.connectedPlayers?.plus(player)
            )
        }
        return player
    }

    fun disconnectPlayer(player: String) {
        playerSockets.remove(player)
        state.update {
            it.copy(
                connectedPlayers = it.connectedPlayers?.minus(player)
            )
        }
    }

    suspend fun broadcast(state: GameState) {
        playerSockets.values.forEach { socket ->
            socket.send(
                Json.encodeToString(state)
            )
        }
    }

    fun updateEditor(value: String) {
        if (value.length > state.value.editor.length) {
            state.update {
                it.copy(
                    editor = value
                )
            }
        } else {
            sleep(10000) // 10 seconds delay
            val allPlayersTyped = playerPositions.values.all { it >= value.length }
            if (!allPlayersTyped) {
                state.update {
                    it.copy(
                        editor = ""
                    )
                }
            }
        }
    }
    fun updatePlayerPosition(player: String, position: Int) {
        playerPositions[player] = position
    }
}