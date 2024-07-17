import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MapperTest {

    @Test
    fun `test mapper`() {
        assertEquals(
            Output("test"),
            Mapper.map(Input("test")),
        )
    }
}