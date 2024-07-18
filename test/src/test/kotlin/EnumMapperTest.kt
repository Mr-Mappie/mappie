import kotlin.test.Test
import kotlin.test.assertEquals

class EnumMapperTest {

    private val mappings = listOf(
        InputEnum.A to OutputEnum.A,
        InputEnum.B to OutputEnum.B,
        InputEnum.C to OutputEnum.C,
        InputEnum.D to OutputEnum.D,
    )

    @Test
    fun `map InputEnum to OutputEnum`() {
        mappings.forEach { (input, expected) ->
            assertEquals(
                EnumMapper.map(input), expected
            )
        }
    }
}