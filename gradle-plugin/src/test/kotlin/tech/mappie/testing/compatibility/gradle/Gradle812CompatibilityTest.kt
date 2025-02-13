package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle812CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "8.12"

    @Test
    fun `test compatibility with gradle 8_12`() {
        runner.withArguments("build").build()
    }
}