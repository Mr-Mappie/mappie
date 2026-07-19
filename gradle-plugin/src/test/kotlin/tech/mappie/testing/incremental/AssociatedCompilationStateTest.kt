package tech.mappie.testing.incremental

import org.junit.jupiter.api.Test
import tech.mappie.testing.TestBase

class AssociatedCompilationStateTest : TestBase() {

    @Test
    fun `test mapper implicitly using a custom mapper defined in the main source set`() {
        kotlin(
            "src/main/kotlin/Models.kt",
            """
            import java.time.LocalDate

            class Iso(val value: String)

            class Range(
                val startsOn: Iso,
                val endsOn: Iso,
            )

            class Period(
                val startsOn: LocalDate,
                val endsOn: LocalDate,
            )
            """.trimIndent()
        )

        kotlin("src/main/kotlin/TimeMappers.kt",
            """
            import tech.mappie.api.ObjectMappie
            import java.time.LocalDate

            object IsoToLocalDateMapper : ObjectMappie<Iso, LocalDate>() {
                override fun map(from: Iso): LocalDate = LocalDate.parse(from.value)
            }
            """.trimIndent())

        kotlin(
            "src/test/kotlin/TestMappers.kt",
            """
            import tech.mappie.api.ObjectMappie

            object RangeToPeriod : ObjectMappie<Range, Period>()
            """.trimIndent()
        )

        kotlin(
            "src/test/kotlin/RangeToPeriodTest.kt",
            """
            import kotlin.test.*

            class RangeToPeriodTest {

                @Test
                fun map() {
                    val period = RangeToPeriod.map(Range(Iso("2026-01-01"), Iso("2026-12-31")))
                    assertEquals(java.time.LocalDate.of(2026, 1, 1), period.startsOn)
                    assertEquals(java.time.LocalDate.of(2026, 12, 31), period.endsOn)
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}
