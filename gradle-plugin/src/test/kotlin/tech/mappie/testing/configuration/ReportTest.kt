package tech.mappie.testing.configuration

import tech.mappie.testing.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ReportTest : TestBase() {

    @Test
    fun `report disabled by default`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            data class Input(val first: String)
            data class Output(val first: String)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()

        assertThat(runner.projectDir.resolve("mappie/Mapper.kt"))
            .doesNotExist()
    }

    @Test
    fun `report enabled`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                reporting {
                    enabled = true
                }
            }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            data class Input(val first: String)
            data class Output(val first: String)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()

        assertThat(runner.projectDir.resolve("mappie/Mapper.kt"))
            .exists()
    }
}