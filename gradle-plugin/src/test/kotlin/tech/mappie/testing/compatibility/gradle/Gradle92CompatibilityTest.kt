package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.Test

class Gradle92CompatibilityTest : GradleCompatibilityTestBase() {

    override val gradleVersion = "9.2.1"

    @Test
    fun `test compatibility with gradle 9_2`() {
        runner.withArguments("build").build()
    }
}