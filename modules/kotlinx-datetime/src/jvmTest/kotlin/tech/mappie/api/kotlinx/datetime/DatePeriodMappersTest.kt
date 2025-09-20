package tech.mappie.api.kotlinx.datetime

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.toJavaPeriod
import kotlinx.datetime.toKotlinDatePeriod
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.time.Period

class DatePeriodMappersTest : MappieTestCase() {

    data class DatePeriodWrapper(val value: DatePeriod)

    data class PeriodWrapper(val value: Period)

    @Test
    fun `map DatePeriod to Period implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.DatePeriodMappersTest.*

                class Mapper : ObjectMappie<DatePeriodWrapper, PeriodWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = DatePeriod()

            val mapper = objectMappie<DatePeriodWrapper, PeriodWrapper>()

            assertThat(mapper.map(DatePeriodWrapper(input)))
                .isEqualTo(PeriodWrapper(input.toJavaPeriod()))
        }
    }

    @Test
    fun `map Period to DatePeriod implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.DatePeriodMappersTest.*

                class Mapper : ObjectMappie<PeriodWrapper, DatePeriodWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = Period.ZERO

            val mapper = objectMappie<PeriodWrapper, DatePeriodWrapper>()

            assertThat(mapper.map(PeriodWrapper(input)))
                .isEqualTo(DatePeriodWrapper(input.toKotlinDatePeriod()))
        }
    }
}