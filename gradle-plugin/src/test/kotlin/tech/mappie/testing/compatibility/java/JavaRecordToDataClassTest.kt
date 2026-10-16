package tech.mappie.testing.compatibility.java

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.TestBase

class JavaRecordToDataClassTest : TestBase() {

    @Test
    fun `map nullable Java record to Kotlin non-null data class`() {
        java("src/main/java/Input.java",
            """
            public record Input(String value) { }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Output.kt",
            """
            data class Output(val value: String) 
            """.trimIndent()
        )
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        kotlin("src/test/kotlin/MapperTest.kt",
            """
            import kotlin.test.*
            
            class MapperTest {
            
                @Test
                fun test() {
                    assertEquals(Output("Hello"), Mapper.map(Input("Hello")))
                }
            }
            """.trimIndent()
        )

        val result = runner.withArguments("build").build()

        assertThat(result.output.lines())
            .anyMatch { it.matches(Regex("w: .+ Target 'Output::value' of type 'Output' is unsafe to assign from 'Input::value' of platform type 'String\\?'\\.")) }
    }
}