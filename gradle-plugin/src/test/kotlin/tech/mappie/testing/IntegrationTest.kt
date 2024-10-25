package tech.mappie.testing

import org.junit.jupiter.api.Test

class IntegrationTest : TestBase() {

    @Test
    fun `the gradle plugin applies the compiler plugin`() {
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
            import kotlin.test.*

            class MapperTest {
            
                @Test
                fun map() {
                    assertEquals(Output("test"), Mapper.map(Input("test")))
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}