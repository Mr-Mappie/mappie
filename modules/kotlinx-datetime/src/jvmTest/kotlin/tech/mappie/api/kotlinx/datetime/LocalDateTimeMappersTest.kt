package tech.mappie.api.kotlinx.datetime

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime as JLocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class LocalDateTimeMappersTest : MappieTestCase() {

    data class LocalDateTimeWrapper(val value: LocalDateTime)

    data class JLocalDateTimeWrapper(val value: JLocalDateTime)

    @Test
    fun `map Kotlin LocalDateTime to Java LocalDateTime implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.LocalDateTimeMappersTest.*

                class Mapper : ObjectMappie<LocalDateTimeWrapper, JLocalDateTimeWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = LocalDateTime(2025, 1, 1, 1, 1, 1)

            val mapper = objectMappie<LocalDateTimeWrapper, JLocalDateTimeWrapper>()

            assertThat(mapper.map(LocalDateTimeWrapper(input)))
                .isEqualTo(JLocalDateTimeWrapper(input.toJavaLocalDateTime()))
        }
    }

    @Test
    fun `map Java LocalDateTime to Kotlin LocalDateTime implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.LocalDateTimeMappersTest.*

                class Mapper : ObjectMappie<JLocalDateTimeWrapper, LocalDateTimeWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = JLocalDateTime.MIN

            val mapper = objectMappie<JLocalDateTimeWrapper, LocalDateTimeWrapper>()

            assertThat(mapper.map(JLocalDateTimeWrapper(input)))
                .isEqualTo(LocalDateTimeWrapper(input.toKotlinLocalDateTime()))
        }
    }
}