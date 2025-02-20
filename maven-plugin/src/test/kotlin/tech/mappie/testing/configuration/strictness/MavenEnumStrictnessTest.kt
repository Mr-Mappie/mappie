package tech.mappie.testing.configuration.strictness

import tech.mappie.testing.MavenTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MavenEnumStrictnessTest : MavenTestBase() {

    override val mappieOptions: Map<String, String> = mapOf(
        "strict-enums" to "true"
    )

    @Test
    fun `not all enum sources must be mapped to a target when disabled`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.EnumMappie
            
            enum class Input { A, B, C }
            enum class Output { A, B }
    
            object Mapper : EnumMappie<Input, Output>()
            """.trimIndent()
        )

        assertThat(execute()).isFailure()
        assertThat(logs.lines())
            .anyMatch { it.matches(Regex("\\[ERROR\\] .+ Source Input.C has no target defined")) }
    }
}