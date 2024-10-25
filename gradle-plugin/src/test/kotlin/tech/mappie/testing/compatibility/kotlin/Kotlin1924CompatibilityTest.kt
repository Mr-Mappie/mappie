package tech.mappie.testing.compatibility.kotlin

import org.junit.jupiter.api.Test

class Kotlin1924CompatibilityTest : KotlinCompatibilityTestBase() {

    override val kotlinVersion = "1.9.24"

    @Test
    fun `test compatibility with kotlin 1_9_24`() {
        runner.withArguments("build").build()
    }
}