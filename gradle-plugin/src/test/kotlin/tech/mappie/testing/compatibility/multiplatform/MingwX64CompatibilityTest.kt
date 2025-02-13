package tech.mappie.testing.compatibility.multiplatform

import org.junit.jupiter.api.Test
import tech.mappie.testing.KotlinPlatform
import tech.mappie.testing.TestBase

class MingwX64CompatibilityTest : TestBase() {

    override val platform = KotlinPlatform.MULTIPLATFORM

    @Test
    fun `test compatibility with mingwX64`() {
        kotlin("src/mingwX64Main/kotlin/CommonMapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class CommonInput(val string: String)
            data class CommonOutput(val string: String)

            object CommonMapper : ObjectMappie<CommonInput, CommonOutput>()
            """.trimIndent()
        )

        kotlin("src/mingwX64Test/kotlin/CommonMapperTest.kt",
            """
            import kotlin.test.*

            class CommonMapperTest {
            
                @Test
                fun `map CommonInput to CommonOutput`() {
                    assertEquals(
                        CommonOutput("value"),
                        CommonMapper.map(CommonInput("value")),
                    )
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}