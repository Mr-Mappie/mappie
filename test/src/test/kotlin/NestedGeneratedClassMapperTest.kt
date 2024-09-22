import kotlin.test.Test
import kotlin.test.assertEquals

class NestedGeneratedClassMapperTest {

    @Test
    fun `map NestedGeneratedInputClassObject to NestedGeneratedOutputClassObject`() {
        assertEquals(
            NestedGeneratedInputClassToOutputClassMapper.map(NestedGeneratedInputClassObject(InputClass(10))),
            NestedGeneratedOutputClassObject(OutputClass(10))
        )
    }
}
