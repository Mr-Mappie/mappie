package testing

import io.github.mappie.api.ObjectMapper

data class Game(
    val name: String,
    val description: String?,
)

data class GameDto(
    val name: String,
    val description: String,
)

object GameMapper : ObjectMapper<Game, GameDto>() {
    override fun map(from: Game): GameDto = mapping {
        GameDto::description mappedFromProperty Game::description transform { it ?: "default" }
    }
}