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

    @Test
    fun `test issue 173 for list`() {
        kotlin(
            "src/commonMain/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            object AMapper : ObjectMappie<AIn, AOut>()
            
            data class AIn(val b: List<BIn>)
            data class AOut(val b: List<BOut>)
            
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
                        AOut(listOf(BOut("value"))),
                        AMapper.map(AIn(listOf(BIn("value")))),
                    )
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }


    @Test
    fun `test issue 173 for nullable`() {
        kotlin(
            "src/commonMain/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            object AMapper : ObjectMappie<AIn, AOut>()
            
            data class AIn(val b: BIn?)
            data class AOut(val b: BOut?)
            
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

                    assertEquals(
                        AOut(null),
                        AMapper.map(AIn(null)),
                    )
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }

    @Test
    fun `test issue 173 for list and nested`() {
        kotlin(
            "src/commonMain/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            object AMapper : ObjectMappie<AIn, AOut>()
            
            data class AIn(val b: List<BIn>, val c: CIn)
            data class AOut(val b: List<BOut>, val c: COut)
            
            data class BIn(val value: String)
            data class BOut(val value: String)
            
            data class CIn(val value: String)
            data class COut(val value: String)
            """.trimIndent()
        )

        kotlin("src/commonTest/kotlin/MapperTest.kt",
            """
            import kotlin.test.*

            class MapperTest {
            
                @Test
                fun `map AIn to AOut`() {
                    assertEquals(
                        AOut(listOf(BOut("list")), COut("value")),
                        AMapper.map(AIn(listOf(BIn("list")), CIn("value"))),
                    )
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}