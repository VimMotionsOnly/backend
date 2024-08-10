package example.com.models

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    var editor: String = "",
    val connectedPlayers: List<String> ?= emptyList(),
)


