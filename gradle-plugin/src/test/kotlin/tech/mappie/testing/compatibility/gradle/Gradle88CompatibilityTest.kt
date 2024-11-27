package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle88CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "8.8"

    @Test
    fun `test compatibility with gradle 8_8`() {
        runner.withArguments("build").build()
    }
}