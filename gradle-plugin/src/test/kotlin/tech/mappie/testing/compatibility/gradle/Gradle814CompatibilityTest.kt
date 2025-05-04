package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle814CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "8.14"

    @Test
    fun `test compatibility with gradle 8_13`() {
        runner.withArguments("build").build()
    }
}