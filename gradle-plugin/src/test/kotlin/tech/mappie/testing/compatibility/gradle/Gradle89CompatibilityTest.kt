package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle89CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "8.9"

    @Test
    fun `test compatibility with gradle 8_9`() {
        runner.withArguments("build").build()
    }
}