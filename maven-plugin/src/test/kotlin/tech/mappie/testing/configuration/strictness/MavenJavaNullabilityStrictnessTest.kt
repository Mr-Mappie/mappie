package tech.mappie.testing.configuration.strictness

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MavenTestBase

class MavenJavaNullabilityStrictnessTest : MavenTestBase() {

    override val mappieOptions = mapOf(
        "strict-java-nullability" to "false"
    )

    @Test
    fun `java nullability warning is not emitted when disabled`() {
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

        assertThat(execute()).isSuccessful()
        assertThat(logs.lines())
            .noneMatch { it.contains("is unsafe to assign") }
    }
}
