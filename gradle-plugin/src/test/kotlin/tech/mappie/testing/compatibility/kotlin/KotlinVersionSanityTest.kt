package tech.mappie.testing.compatibility.kotlin

import org.junit.jupiter.api.Test

class KotlinVersionSanityTest : KotlinCompatibilityTestBase() {

    override val kotlinVersion = "does not exist"

    @Test
    fun `test compatibility with kotlin non-existing version fails`() {
        runner.withArguments("build").buildAndFail()
    }
}