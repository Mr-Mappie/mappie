package tech.mappie.testing.modules

import tech.mappie.testing.TestBase
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieModules.MODULE_KOTLINX_DATETIME

class KotlinxDateTimeTest : TestBase() {

    override val modules = setOf(MODULE_KOTLINX_DATETIME)

    @Test
    fun `module kotlinx-datetime can be used`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            import java.time.Period
            import kotlinx.datetime.DatePeriod
            
            data class Input(val first: Period)
            data class Output(val first: DatePeriod)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}