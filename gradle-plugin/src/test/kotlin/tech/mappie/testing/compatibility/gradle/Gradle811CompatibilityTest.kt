package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle811CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "8.11.1"

    @Test
    fun `test compatibility with gradle 8_11_1`() {
        runner.withArguments("build").build()
    }
}