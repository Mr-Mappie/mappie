package tech.mappie.testing.compatibility.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.BuildConfig

class OlderKotlinVersionCompatibilityTest : KotlinCompatibilityTestBase() {

    override val kotlinVersion = "2.2.0"

    @Test
    fun `test compatibility with kotlin 2_2_0`() {
        val result = runner.withArguments("build").run()

        val exptectedVersion = BuildConfig.VERSION.split('-').first()

        assertThat(result.output.lines())
            .anyMatch { it == "Mappie unsupported Kotlin version $kotlinVersion, $exptectedVersion was expected. This is highly likely to lead to compilation failure." }
    }
}