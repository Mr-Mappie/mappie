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
            import java.time.Period
            import kotlinx.datetime.DatePeriod
            import tech.mappie.api.kotlinx.datetime.JavaPeriodToKotlinDatePeriodMapper
            
            data class Input(val first: Period)
            data class Output(val first: DatePeriod)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        kotlin("src/jvmTest/kotlin/MapperTest.kt",
            """
            import kotlin.test.*
            import java.time.Period
            import kotlinx.datetime.DatePeriod
            import kotlinx.datetime.toKotlinDatePeriod

            class JvmMapperTest {

                @Test
                fun `map Input to Output`() {
                    assertEquals(
                        Output(Period.ZERO.toKotlinDatePeriod()),
                        Mapper.map(Input(Period.ZERO)),
                    )
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}