package tech.mappie.testing.configuration

import tech.mappie.testing.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WarningsAsErrorsTest : TestBase() {

    @Test
    fun `an expected warning is output as a warning by default`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.EnumMappie
            
            enum class Input { A, B }
            enum class Output { A, B }
    
            object Mapper : EnumMappie<Input, Output>() {
                override fun map(from: Input) = mapping {
                    Output.A fromEnumEntry Input.A
                }
            }
            """.trimIndent()
        )

        val result = runner.withArguments("build").build()

        assertThat(result.output.lines())
            .anyMatch { it.matches(Regex("w: .+ Unnecessary explicit mapping of source 'Input.A'")) }
    }
}