package tech.mappie.api.kotlinx.datetime

import java.time.DayOfWeek as JDayOfWeek
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toJavaDayOfWeek
import kotlinx.datetime.toKotlinDayOfWeek
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class DayOfWeekMappersTest : MappieTestCase() {

    data class DayOfWeekWrapper(val value: DayOfWeek)

    data class JDayOfWeekWrapper(val value: JDayOfWeek)

    @Test
    fun `map Kotlin DayOfWeek to Java DayOfWeek implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.DayOfWeekMappersTest.*

                class Mapper : ObjectMappie<DayOfWeekWrapper, JDayOfWeekWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = DayOfWeek.THURSDAY

            val mapper = objectMappie<DayOfWeekWrapper, JDayOfWeekWrapper>()

            assertThat(mapper.map(DayOfWeekWrapper(input)))
                .isEqualTo(JDayOfWeekWrapper(input.toJavaDayOfWeek()))
        }
    }

    @Test
    fun `map Java DayOfWeek to Kotlin DayOfWeek implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.DayOfWeekMappersTest.*

                class Mapper : ObjectMappie<JDayOfWeekWrapper, DayOfWeekWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = JDayOfWeek.FRIDAY

            val mapper = objectMappie<JDayOfWeekWrapper, DayOfWeekWrapper>()

            assertThat(mapper.map(JDayOfWeekWrapper(input)))
                .isEqualTo(DayOfWeekWrapper(input.toKotlinDayOfWeek()))
        }
    }
}