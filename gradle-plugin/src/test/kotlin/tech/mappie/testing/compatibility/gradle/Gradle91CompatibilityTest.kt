package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle91CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "9.1.0"

    @Test
    fun `test compatibility with gradle 9_1`() {
        runner.withArguments("build").build()
    }
}