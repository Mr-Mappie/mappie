package tech.mappie.testing

import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat

class IntegrationTest : TestBase() {

    @Test
    fun `the maven plugin applies the compiler plugin`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class Input(val value: String)
            data class Output(val value: String)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        kotlin("src/test/kotlin/MapperTest.kt",
            """
            import org.testng.Assert.assertEquals
            import org.testng.annotations.*

            class MapperTest {
            
                @Test
                fun map() {
                    assertEquals(Output("test"), Mapper.map(Input("test")))
                }
            }
            """.trimIndent()
        )

        assertThat(execute()).isSuccessful()
    }
}