package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle80CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "8.0.2"

    @Test
    fun `test compatibility with gradle 8_0_2`() {
        runner.withArguments("build").build()
    }
}