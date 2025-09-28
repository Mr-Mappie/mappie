package tech.mappie.api.kotlinx.datetime

import java.time.Month as JMonth
import kotlinx.datetime.Month
import kotlinx.datetime.toJavaMonth
import kotlinx.datetime.toKotlinMonth
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class MonthMappersTest : MappieTestCase() {

    data class MonthWrapper(val value: Month)

    data class JMonthWrapper(val value: JMonth)

    @Test
    fun `map Kotlin Month to Java Month implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.MonthMappersTest.*

                class Mapper : ObjectMappie<MonthWrapper, JMonthWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = Month.FEBRUARY

            val mapper = objectMappie<MonthWrapper, JMonthWrapper>()

            assertThat(mapper.map(MonthWrapper(input)))
                .isEqualTo(JMonthWrapper(input.toJavaMonth()))
        }
    }

    @Test
    fun `map Java Month to Kotlin Month implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.MonthMappersTest.*

                class Mapper : ObjectMappie<JMonthWrapper, MonthWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = JMonth.AUGUST

            val mapper = objectMappie<JMonthWrapper, MonthWrapper>()

            assertThat(mapper.map(JMonthWrapper(input)))
                .isEqualTo(MonthWrapper(input.toKotlinMonth()))
        }
    }
}