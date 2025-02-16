package tech.mappie.testing.compatibility.multiplatform

import org.junit.jupiter.api.Test
import tech.mappie.testing.KotlinPlatform
import tech.mappie.testing.TestBase

class MingwX64CompatibilityTest : TestBase() {

    override val platform = KotlinPlatform.MULTIPLATFORM

    @Test
    fun `test compatibility with mingwX64`() {
        kotlin("src/mingwX64Main/kotlin/NativeMapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class NativeInput(val string: String)
            data class NativeOutput(val string: String)

            object NativeMapper : ObjectMappie<NativeInput, NativeOutput>()
            """.trimIndent()
        )

        kotlin("src/mingwX64Test/kotlin/NativeMapperTest.kt",
            """
            import kotlin.test.*

            class NativeMapperTest {
            
                @Test
                fun `map NativeInput to NativeOutput`() {
                    assertEquals(
                        NativeOutput("value"),
                        NativeMapper.map(NativeInput("value")),
                    )
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}