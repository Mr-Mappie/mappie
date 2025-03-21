package tech.mappie.testing.issues

import org.junit.jupiter.api.Test
import tech.mappie.testing.KotlinPlatform
import tech.mappie.testing.TestBase

class Issue173 : TestBase() {

    override val platform = KotlinPlatform.MULTIPLATFORM

    @Test
    fun `test issue 173`() {
        kotlin(
            "src/commonMain/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            object AMapper : ObjectMappie<AIn, AOut>()
            
            data class AIn(val b: BIn)
            data class AOut(val b: BOut)
            
            data class BIn(val value: String)
            data class BOut(val value: String)
            """.trimIndent()
        )

        kotlin("src/commonTest/kotlin/MapperTest.kt",
            """
            import kotlin.test.*

            class MapperTest {
            
                @Test
                fun `map AIn to AOut`() {
                    assertEquals(
                        AOut(BOut("value")),
                        AMapper.map(AIn(BIn("value"))),
                    )
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}