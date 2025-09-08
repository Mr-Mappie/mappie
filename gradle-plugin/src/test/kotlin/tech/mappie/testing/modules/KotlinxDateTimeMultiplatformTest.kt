package tech.mappie.testing.modules

import tech.mappie.testing.TestBase
import org.junit.jupiter.api.Test
import tech.mappie.testing.KotlinPlatform
import tech.mappie.testing.MappieModules.MODULE_KOTLINX_DATETIME

class KotlinxDateTimeMultiplatformTest : TestBase() {

    override val platform = KotlinPlatform.MULTIPLATFORM

    override val modules = setOf(MODULE_KOTLINX_DATETIME)

    @Test
    fun `module kotlinx-datetime can be used in multiplatform`() {
        kotlin("src/jvmMain/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            import java.time.DayOfWeek as JDayOfWeek
            import kotlinx.datetime.DayOfWeek
            
            data class Input(val first: JDayOfWeek)
            data class Output(val first: DayOfWeek)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        kotlin("src/jvmTest/kotlin/MapperTest.kt",
            """
            import kotlin.test.*
            import java.time.DayOfWeek as JDayOfWeek
            import kotlinx.datetime.DayOfWeek

            class JvmMapperTest {

                @Test
                fun `map Input to Output`() {
                    assertEquals(
                        Output(DayOfWeek.SATURDAY),
                        Mapper.map(Input(JDayOfWeek.SATURDAY)),
                    )
                }
            }
            """.trimIndent()
        )


        runner.withArguments("build").build()
    }
}