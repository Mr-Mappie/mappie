package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
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
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.LocalDateTimeMappersTest.*

                        class Mapper : ObjectMappie<LocalDateTimeInput, LocalTimeOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

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
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
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
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

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
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.LocalDateTimeMappersTest.*

                        class Mapper : ObjectMappie<LocalDateTimeInput, LocalDateOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

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
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
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
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

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