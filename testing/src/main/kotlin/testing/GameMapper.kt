package testing

import io.github.mappie.api.DataClassMapper

data class Game(
    val name: String,
    val description: String?,
)

data class GameDto(
    val name: String,
    val description: String,
)

object GameMapper : DataClassMapper<Game, GameDto>() {
    override fun map(from: Game): GameDto = mapping {
        GameDto::description mappedFromProperty Game::description transform { it ?: "default" }
    }
}