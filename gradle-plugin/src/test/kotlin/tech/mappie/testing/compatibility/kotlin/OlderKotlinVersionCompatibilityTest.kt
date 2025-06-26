package tech.mappie.testing.compatibility.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OlderKotlinVersionCompatibilityTest : KotlinCompatibilityTestBase() {

    override val kotlinVersion = "2.1.10"

    @Test
    fun `test compatibility with kotlin 2_1_0`() {
        val result = runner.withArguments("build").buildAndFail()

        assertThat(result.output.lines())
            .anyMatch { it == "Mappie unsupported Kotlin version 2.1.10, 2.2.0 was expected. This is highly likely to lead to compilation failure." }
    }
}