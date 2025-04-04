package tech.mappie.testing.configuration.strictness

import tech.mappie.testing.MavenTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MavenVisibilityStrictnessTest : MavenTestBase() {

    override val mappieOptions = mapOf(
        "strict-visibility" to "false"
    )

    @Test
    fun `non-visible constructors can be used when disabled`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            data class Input(val value: String)
            @ConsistentCopyVisibility
            data class Output private constructor(val value: String)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        assertThat(execute()).isSuccessful()
    }
}