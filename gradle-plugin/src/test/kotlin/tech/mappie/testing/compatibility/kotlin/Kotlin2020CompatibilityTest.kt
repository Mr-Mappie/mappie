package tech.mappie.testing.compatibility.kotlin

import org.junit.jupiter.api.Test

class Kotlin2020CompatibilityTest : KotlinCompatibilityTestBase() {

    override val kotlinVersion = "2.0.20"

    @Test
    fun `test compatibility with kotlin 2_0_20`() {
        runner.withArguments("build", "--stacktrace").build()
    }
}