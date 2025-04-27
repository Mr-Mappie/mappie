package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class LocalDateTimeMappersTest {

    @TempDir
    lateinit var directory: File

    data class LocalDateTimeInput(val value: LocalDateTime)

    data class LocalTimeOutput(val value: LocalTime)

    data class LocalDateOutput(val value: LocalDate)

    @Test
    fun `map LocalDateTime to LocalTime implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.LocalDateTimeMappersTest.*

                class Mapper : ObjectMappie<LocalDateTimeInput, LocalTimeOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = LocalDateTime.now()

            val mapper = classLoader
                .loadObjectMappieClass<LocalDateTimeInput, LocalTimeOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(LocalDateTimeInput(input)))
                .isEqualTo(LocalTimeOutput(input.toLocalTime()))
        }
    }

    @Test
    fun `map LocalDateTime to LocalTime explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.LocalDateTimeMappersTest.*

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

            val mapper = classLoader
                .loadObjectMappieClass<LocalDateTimeInput, LocalTimeOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(LocalDateTimeInput(input)))
                .isEqualTo(LocalTimeOutput(input.toLocalTime()))
        }
    }

    @Test
    fun `map LocalDateTime to LocalDate implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.LocalDateTimeMappersTest.*

                class Mapper : ObjectMappie<LocalDateTimeInput, LocalDateOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = LocalDateTime.now()

            val mapper = classLoader
                .loadObjectMappieClass<LocalDateTimeInput, LocalDateOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(LocalDateTimeInput(input)))
                .isEqualTo(LocalDateOutput(input.toLocalDate()))
        }
    }

    @Test
    fun `map LocalDateTime to LocalDate explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.LocalDateTimeMappersTest.*

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

            val mapper = classLoader
                .loadObjectMappieClass<LocalDateTimeInput, LocalDateOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(LocalDateTimeInput(input)))
                .isEqualTo(LocalDateOutput(input.toLocalDate()))
        }
    }
}