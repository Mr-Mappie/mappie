package tech.mappie.testing.configuration.strictness

import tech.mappie.testing.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VisibilityStrictnessTest : TestBase() {

    @Test
    fun `non-visible constructors can be used default`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            data class Input(val value: String)
            data class Output private constructor(val value: String)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }

    @Test
    fun `non-visible constructors can be used when disabled`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                strictness {
                    visibility = false        
                }
            }                
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            data class Input(val value: String)
            data class Output private constructor(val value: String)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }

    @Test
    fun `non-visible constructors cannot be used when disabled`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                strictness {
                    visibility = true        
                }
            }                
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            data class Input(val value: String)
            data class Output private constructor(val value: String)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        val result = runner.withArguments("build").buildAndFail()

        assertThat(result.output.lines())
            .anyMatch { it.matches(Regex("e: .+ Constructor Output\\(value: String\\) is not visible from the current scope")) }
    }

    @Test
    fun `non-visible constructors can be used when disabled globally but enabled locally`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                strictness {
                    visibility = true        
                }
            }                
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            import tech.mappie.api.config.UseStrictVisibility
            
            data class Input(val value: String)
            data class Output private constructor(val value: String)
    
            @UseStrictVisibility(false)
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}