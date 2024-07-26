import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ObjectMapperTest {

    @Test
    fun `map using ObjectMapper`() {
        assertEquals(
            OutputObject("name", 22, true),
            ObjectMapper.map(InputObject("name", 22, NestedInput(NestedInput.BooleanEnum.TRUE)))
        )
    }
}