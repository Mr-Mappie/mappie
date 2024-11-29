package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle810CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "8.10.2"

    @Test
    fun `test compatibility with gradle 8_10_2`() {
        runner.withArguments("build").build()
    }
}