package tech.mappie.testing.configuration

import tech.mappie.testing.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CaseInsensitiveMatchingTest : TestBase() {

    @Test
    fun `case-insensitive matching is disabled by default`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class Input(val user_name: String)
            data class Output(val userName: String)

            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        val result = runner.withArguments("build").buildAndFail()

        assertThat(result.output.lines())
            .anyMatch { it.matches(Regex("e: .+ Target Output::userName has no source defined")) }
    }

    @Test
    fun `case-insensitive matching works when enabled globally`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                useCaseInsensitiveMatching = true
            }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class Input(val user_name: String)
            data class Output(val userName: String)

            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }

    @Test
    fun `case-insensitive matching works with annotation`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            import tech.mappie.api.config.UseCaseInsensitiveMatching

            data class Input(val user_name: String)
            data class Output(val userName: String)

            @UseCaseInsensitiveMatching
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }

    @Test
    fun `case-insensitive matching can be disabled locally when enabled globally`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                useCaseInsensitiveMatching = true
            }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            import tech.mappie.api.config.UseCaseInsensitiveMatching

            data class Input(val user_name: String)
            data class Output(val userName: String)

            @UseCaseInsensitiveMatching(false)
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        val result = runner.withArguments("build").buildAndFail()

        assertThat(result.output.lines())
            .anyMatch { it.matches(Regex("e: .+ Target Output::userName has no source defined")) }
    }

    @Test
    fun `exact match takes priority over case-insensitive match`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                useCaseInsensitiveMatching = true
            }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class Input(val userName: String, val user_name: String)
            data class Output(val userName: String)

            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }

    @Test
    fun `multiple sources with same normalized name report error with source names`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                useCaseInsensitiveMatching = true
            }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class Input(val user_name: String, val UserName: String)
            data class Output(val username: String)

            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        val result = runner.withArguments("build").buildAndFail()

        assertThat(result.output.lines())
            .anyMatch { it.matches(Regex("e: .+ Target Output::username has multiple sources defined: .*")) }
    }

    @Test
    fun `kebab-case to camelCase matching works`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                useCaseInsensitiveMatching = true
            }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class Input(@get:JvmName("getUser-name") val `user-name`: String)
            data class Output(val userName: String)

            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}
