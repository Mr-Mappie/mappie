package tech.mappie.api.kotlinx.datetime

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate as JLocalDate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class LocalDateMappersTest : MappieTestCase() {

    data class LocalDateWrapper(val value: LocalDate)

    data class JLocalDateWrapper(val value: JLocalDate)

    @Test
    fun `map Kotlin LocalDate to Java LocalDate implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.LocalDateMappersTest.*

                class Mapper : ObjectMappie<LocalDateWrapper, JLocalDateWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = LocalDate.fromEpochDays(1)

            val mapper = objectMappie<LocalDateWrapper, JLocalDateWrapper>()

            assertThat(mapper.map(LocalDateWrapper(input)))
                .isEqualTo(JLocalDateWrapper(input.toJavaLocalDate()))
        }
    }

    @Test
    fun `map Java LocalDate to Kotlin LocalDate implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.LocalDateMappersTest.*

                class Mapper : ObjectMappie<JLocalDateWrapper, LocalDateWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = JLocalDate.EPOCH

            val mapper = objectMappie<JLocalDateWrapper, LocalDateWrapper>()

            assertThat(mapper.map(JLocalDateWrapper(input)))
                .isEqualTo(LocalDateWrapper(input.toKotlinLocalDate()))
        }
    }
}