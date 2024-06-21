package testing

import io.github.mappie.api.ObjectMappie

data class Game(
    val name: String,
    val description: String?,
)

data class GameDto(
    val name: String,
    val description: String,
)

object GameMapper : ObjectMappie<Game, GameDto>() {
    override fun map(from: Game): GameDto = mapping {
        GameDto::description fromProperty Game::description transform { it ?: "default" }
    }
}