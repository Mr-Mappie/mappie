package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ShortMappersTest {

    @TempDir
    lateinit var directory: File

    data class ShortInput(val value: Short)

    data class IntOutput(val value: Int)

    data class LongOutput(val value: Long)

    @Test
    fun `map Short to Int implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.ShortMappersTest.*

                        class Mapper : ObjectMappie<ShortInput, IntOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Short = 2

            val mapper = classLoader
                .loadObjectMappieClass<ShortInput, IntOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(IntOutput(input.toInt()))
        }
    }

    @Test
    fun `map Short to Int explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.ShortMappersTest.*

                        class Mapper : ObjectMappie<ShortInput, IntOutput>() {
                            override fun map(from: ShortInput) = mapping {
                                to::value fromProperty from::value via ShortToIntMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Short = 5

            val mapper = classLoader
                .loadObjectMappieClass<ShortInput, IntOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(IntOutput(input.toInt()))
        }
    }

    @Test
    fun `map Short to Long implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.ShortMappersTest.*

                        class Mapper : ObjectMappie<ShortInput, LongOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Short = 2

            val mapper = classLoader
                .loadObjectMappieClass<ShortInput, LongOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(LongOutput(input.toLong()))
        }
    }

    @Test
    fun `map Short to Long explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.ShortMappersTest.*

                        class Mapper : ObjectMappie<ShortInput, LongOutput>() {
                            override fun map(from: ShortInput) = mapping {
                                to::value fromProperty from::value via ShortToLongMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Short = 5

            val mapper = classLoader
                .loadObjectMappieClass<ShortInput, LongOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(LongOutput(input.toLong()))
        }
    }
}