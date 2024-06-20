package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameMapperTest {

    @Test
    fun `map Game to GameDto with null via GameMapper`() {
        assertEquals(
            GameDto("Poker", "default"),
            GameMapper.map(Game("Poker", null)),
        )
    }

    @Test
    fun `map Game to GameDto without null via GameMapper`() {
        assertEquals(
            GameDto("Poker", "description"),
            GameMapper.map(Game("Poker", "description")),
        )
    }
}