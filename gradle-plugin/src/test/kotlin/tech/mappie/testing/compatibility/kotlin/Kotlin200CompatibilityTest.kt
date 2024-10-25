package tech.mappie.testing.compatibility.kotlin

import org.junit.jupiter.api.Test

class Kotlin200CompatibilityTest : KotlinCompatibilityTestBase() {

    override val kotlinVersion = "2.0.0"

    @Test
    fun `test compatibility with kotlin 2_0_0`() {
        runner.withArguments("build").build()
    }
}