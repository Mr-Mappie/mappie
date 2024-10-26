package tech.mappie.testing.configuration.strictness

import tech.mappie.testing.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EnumStrictnessTest : TestBase() {

    @Test
    fun `all enum sources must be mapped to a target default`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.EnumMappie
            
            enum class Input { A, B, C }
            enum class Output { A, B }
    
            object Mapper : EnumMappie<Input, Output>()
            """.trimIndent()
        )

        val result = runner.withArguments("build").buildAndFail()

        assertThat(result.output.lines())
            .anyMatch { it.matches(Regex("e: .+ Source Input.C has no target defined")) }
    }

    @Test
    fun `not all enum sources must be mapped to a target when disabled`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                strictness {
                    enums = false        
                }
            }                
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.EnumMappie
            
            enum class Input { A, B, C }
            enum class Output { A, B }
    
            object Mapper : EnumMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }

    @Test
    fun `not all enum sources must be mapped to a target when disabled globally but enabled locally`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                strictness {
                    enums = true        
                }
            }                
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.EnumMappie
            import tech.mappie.api.config.UseStrictEnums
            
            enum class Input { A, B, C }
            enum class Output { A, B }
            
            @UseStrictEnums(false)
            object Mapper : EnumMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}