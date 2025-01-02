package tech.mappie.testing.compatibility.multiplatform

import org.junit.jupiter.api.Test
import tech.mappie.testing.KotlinPlatform
import tech.mappie.testing.TestBase

class MultiplatformCompatibilityTest : TestBase() {

    override val platform = KotlinPlatform.MULTIPLATFORM

    @Test
    fun `test compatibility with multiplatform`() {
        kotlin("src/commonMain/kotlin/CommonMapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class CommonInput(val string: String)
            data class CommonOutput(val string: String)

            object CommonMapper : ObjectMappie<CommonInput, CommonOutput>()
            """.trimIndent()
        )

        kotlin("src/commonTest/kotlin/CommonMapperTest.kt",
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

        kotlin("src/jvmMain/kotlin/JvmMapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class JvmInput(val string: String, val int: Int)
            data class JvmOutput(val string: String, val int: Int)

            object JvmMapper : ObjectMappie<JvmInput, JvmOutput>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}