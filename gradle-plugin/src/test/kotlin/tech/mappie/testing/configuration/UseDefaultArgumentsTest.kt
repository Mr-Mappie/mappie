package tech.mappie.testing.configuration

import tech.mappie.testing.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UseDefaultArgumentsTest : TestBase() {

    @Test
    fun `default arguments are used by default`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            data class Input(val first: String)
            data class Output(val first: String, val second: Int = 1)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }

    @Test
    fun `default arguments are not used when disabled`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                useDefaultArguments = false
            }                
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            data class Input(val first: String)
            data class Output(val first: String, val second: Int = 1)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        val result = runner.withArguments("build").buildAndFail()

        assertThat(result.output.lines())
            .anyMatch { it.matches(Regex("e: .+ Target Output::second has no source defined")) }
    }

    @Test
    fun `default arguments are used when disabled globally but enabled locally`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                useDefaultArguments = false
            }                
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            import tech.mappie.api.config.UseDefaultArguments
            
            data class Input(val first: String)
            data class Output(val first: String, val second: Int = 1)
    
            @UseDefaultArguments
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}