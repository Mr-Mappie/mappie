import kotlin.test.Test
import kotlin.test.assertEquals

class NestedGeneratedEnumMapperTest {

    private val mappings = listOf(
        InputEnum.A to OutputEnum.A,
        InputEnum.B to OutputEnum.B,
        InputEnum.C to OutputEnum.C,
        InputEnum.D to OutputEnum.D,
    )

    @Test
    fun `map NestedGeneratedInputObject to NestedGeneratedOutputObject`() {
        mappings.forEach { (input, expected) ->
            assertEquals(
                NestedGeneratedInputToOutputMapper.map(NestedGeneratedInputObject(input)),
                NestedGeneratedOutputObject(expected)
            )
        }
    }
}