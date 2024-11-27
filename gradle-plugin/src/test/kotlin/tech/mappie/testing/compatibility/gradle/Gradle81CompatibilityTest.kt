package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle81CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "8.1.1"

    @Test
    fun `test compatibility with gradle 8_1_1`() {
        runner.withArguments("build").build()
    }
}