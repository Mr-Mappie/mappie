package tech.mappie.testing.compatibility.multiplatform

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import tech.mappie.testing.KotlinPlatform
import tech.mappie.testing.TestBase

@EnabledOnOs(OS.MAC)
class IOSCompatibilityTest : TestBase() {

    override val platform = KotlinPlatform.MULTIPLATFORM

    @Test
    fun `test compatibility with ios`() {
        kotlin("src/iosMain/kotlin/IOSMapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class IOSInput(val string: String)
            data class IOSOutput(val string: String)

            object IOSMapper : ObjectMappie<IOSInput, IOSOutput>()
            """.trimIndent()
        )

        kotlin("src/iosTest/kotlin/IOSMapperTest.kt",
            """
            import kotlin.test.*

            class CommonMapperTest {
            
                @Test
                fun `map CommonInput to CommonOutput`() {
                    assertEquals(
                        IOSOutput("value"),
                        IOSMapper.map(IOSInput("value")),
                    )
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}