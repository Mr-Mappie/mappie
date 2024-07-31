import kotlin.test.Test
import kotlin.test.assertEquals

class NestedGeneratedEnumMapperTest {

    private val mappings = listOf(
        InputEnumTwo.A to OutputEnumTwo.A,
        InputEnumTwo.B to OutputEnumTwo.B,
        InputEnumTwo.C to OutputEnumTwo.C,
        InputEnumTwo.D to OutputEnumTwo.D,
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
