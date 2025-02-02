package tech.mappie.testing.compatibility.kotlin

import org.junit.jupiter.api.Test

class Kotlin2110CompatibilityTest : KotlinCompatibilityTestBase() {

    override val kotlinVersion = "2.1.10"

    @Test
    fun `test compatibility with kotlin 2_1_0`() {
        runner.withArguments("build").build()
    }
}