package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle813CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "8.13"

    @Test
    fun `test compatibility with gradle 8_13`() {
        runner.withArguments("build").build()
    }
}