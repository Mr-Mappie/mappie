package tech.mappie.testing.compatibility.kotlin

import org.junit.jupiter.api.Test

class Kotlin2021CompatibilityTest : KotlinCompatibilityTestBase() {

    override val kotlinVersion = "2.0.21"

    @Test
    fun `test compatibility with kotlin 2_0_21`() {
        runner.withArguments("build").build()
    }
}