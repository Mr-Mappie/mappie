package tech.mappie.testing.configuration.strictness

import tech.mappie.testing.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MavenEnumStrictnessTest : TestBase() {

    override val mappieOptions: Map<String, String> = mapOf(
        "strictness.enums" to "true"
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

        assertThat(execute()).isSuccessful()
        assertThat(logs.lines())
            .noneMatch { it.matches(Regex("\\[WARNING\\]")) }
    }
}