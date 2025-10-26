package tech.mappie.testing.configuration.strictness

import tech.mappie.testing.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class platformTypeNullabilityStrictnessTest : TestBase() {

    @Test
    fun `java nullability warning is emitted when enabled`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                strictness {
                    platformTypeNullability = true        
                }
            }                
            """.trimIndent()
        )

        java("src/main/java/Input.java",
            """
            public class Input {
                private String value;

                public Input(String value) {
                    this.value = value;
                }

                public String getValue() {
                    return value;
                }
            }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class Output(val value: String)

            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        val result = runner.withArguments("build").build()

        assertThat(result.output.lines())
            .anyMatch { it.contains("is unsafe to assign") }
    }

    @Test
    fun `java nullability warning is not emitted when disabled`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                strictness {
                    platformTypeNullability = false        
                }
            }                
            """.trimIndent()
        )

        java("src/main/java/Input.java",
            """
            public class Input {
                private String value;

                public Input(String value) {
                    this.value = value;
                }

                public String getValue() {
                    return value;
                }
            }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class Output(val value: String)

            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        val result = runner.withArguments("build").build()

        assertThat(result.output.lines())
            .noneMatch { it.contains("is unsafe to assign") }
    }

    @Test
    fun `java nullability warning is not emitted when disabled locally`() {
        kotlin("build.gradle.kts",
            """
            mappie {
                strictness {
                    platformTypeNullability = true        
                }
            }                
            """.trimIndent()
        )

        java("src/main/java/Input.java",
            """
            public class Input {
                private String value;

                public Input(String value) {
                    this.value = value;
                }

                public String getValue() {
                    return value;
                }
            }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            import tech.mappie.api.config.UseStrictPlatformTypeNullabilityValidation

            data class Output(val value: String)

            @UseStrictPlatformTypeNullabilityValidation(false)
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        val result = runner.withArguments("build").build()

        assertThat(result.output.lines())
            .noneMatch { it.contains("is unsafe to assign") }
    }
}