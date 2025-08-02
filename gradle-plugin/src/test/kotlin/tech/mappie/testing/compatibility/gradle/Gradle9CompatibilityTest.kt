package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle9CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "9.0.0"

    @Test
    fun `test compatibility with gradle 9`() {
        runner.withArguments("build").build()
    }
}