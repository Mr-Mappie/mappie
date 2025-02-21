package tech.mappie.testing.configuration

import tech.mappie.testing.MavenTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MavenUseDefaultArgumentsTest : MavenTestBase() {

    override val mappieOptions = mapOf(
        "use-default-arguments" to "false"
    )

    @Test
    fun `default arguments are not used when disabled`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            data class Input(val first: String)
            data class Output(val first: String, val second: Int = 1)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        assertThat(execute()).isFailure()
        assertThat(logs.lines())
            .anyMatch { it.matches(Regex("\\[ERROR\\] .+ Target Output::second has no source defined")) }
    }
}