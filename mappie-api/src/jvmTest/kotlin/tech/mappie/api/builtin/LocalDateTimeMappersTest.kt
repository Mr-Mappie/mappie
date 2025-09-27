package tech.mappie.api.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class LocalDateTimeMappersTest : MappieTestCase() {

    data class LocalDateTimeInput(val value: LocalDateTime)

    data class LocalTimeOutput(val value: LocalTime)

    data class LocalDateOutput(val value: LocalDate)

    @Test
    fun `map LocalDateTime to LocalTime implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.LocalDateTimeMappersTest.*

                class Mapper : ObjectMappie<LocalDateTimeInput, LocalTimeOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = LocalDateTime.now()

            val mapper = objectMappie<LocalDateTimeInput, LocalTimeOutput>()

            assertThat(mapper.map(LocalDateTimeInput(input)))
                .isEqualTo(LocalTimeOutput(input.toLocalTime()))
        }
    }

    @Test
    fun `map LocalDateTime to LocalTime explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.LocalDateTimeMappersTest.*

                class Mapper : ObjectMappie<LocalDateTimeInput, LocalTimeOutput>() {
                    override fun map(from: LocalDateTimeInput) = mapping {
                        to::value fromProperty from::value via LocalDateTimeToLocalTimeMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = LocalDateTime.now()

            val mapper = objectMappie<LocalDateTimeInput, LocalTimeOutput>()

            assertThat(mapper.map(LocalDateTimeInput(input)))
                .isEqualTo(LocalTimeOutput(input.toLocalTime()))
        }
    }

    @Test
    fun `map LocalDateTime to LocalDate implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.LocalDateTimeMappersTest.*

                class Mapper : ObjectMappie<LocalDateTimeInput, LocalDateOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = LocalDateTime.now()

            val mapper = objectMappie<LocalDateTimeInput, LocalDateOutput>()

            assertThat(mapper.map(LocalDateTimeInput(input)))
                .isEqualTo(LocalDateOutput(input.toLocalDate()))
        }
    }

    @Test
    fun `map LocalDateTime to LocalDate explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.LocalDateTimeMappersTest.*

                class Mapper : ObjectMappie<LocalDateTimeInput, LocalDateOutput>() {
                    override fun map(from: LocalDateTimeInput) = mapping {
                        to::value fromProperty from::value via LocalDateTimeToLocalDateMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = LocalDateTime.now()

            val mapper = objectMappie<LocalDateTimeInput, LocalDateOutput>()

            assertThat(mapper.map(LocalDateTimeInput(input)))
                .isEqualTo(LocalDateOutput(input.toLocalDate()))
        }
    }
}