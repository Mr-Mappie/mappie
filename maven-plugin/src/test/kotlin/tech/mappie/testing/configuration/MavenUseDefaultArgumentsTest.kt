package tech.mappie.testing.configuration

import tech.mappie.testing.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MavenUseDefaultArgumentsTest : TestBase() {

    override val mappieOptions = mapOf(
        "useDefaultArguments" to "false"
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

        assertThat(execute()).isSuccessful()
        assertThat(logs.lines())
            .anyMatch { it.matches(Regex("\\[WARNING\\]: .+ Target Output::second has no source defined")) }
    }
}