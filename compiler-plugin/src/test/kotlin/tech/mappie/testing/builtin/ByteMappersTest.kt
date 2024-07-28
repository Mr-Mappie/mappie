package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ByteMappersTest {

    @TempDir
    lateinit var directory: File

    data class ByteInput(val value: Byte)

    data class ShortOutput(val value: Short)

    data class IntOutput(val value: Int)

    data class LongOutput(val value: Long)

    @Test
    fun `map Short to Short implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.ByteMappersTest.*

                        class Mapper : ObjectMappie<ByteInput, ShortOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Byte = 2

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, ShortOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(ShortOutput(input.toShort()))
        }
    }

    @Test
    fun `map Short to Short explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.ByteMappersTest.*

                        class Mapper : ObjectMappie<ByteInput, ShortOutput>() {
                            override fun map(from: ByteInput) = mapping {
                                to::value fromProperty from::value via ByteToShortMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Byte = 5

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, ShortOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(ShortOutput(input.toShort()))
        }
    }

    @Test
    fun `map Short to Int implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.ByteMappersTest.*

                        class Mapper : ObjectMappie<ByteInput, IntOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Byte = 2

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, IntOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
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
                        import tech.mappie.testing.builtin.ByteMappersTest.*

                        class Mapper : ObjectMappie<ByteInput, IntOutput>() {
                            override fun map(from: ByteInput) = mapping {
                                to::value fromProperty from::value via ByteToIntMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Byte = 5

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, IntOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
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
                        import tech.mappie.testing.builtin.ByteMappersTest.*

                        class Mapper : ObjectMappie<ByteInput, LongOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Byte = 2

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, LongOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
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
                        import tech.mappie.testing.builtin.ByteMappersTest.*

                        class Mapper : ObjectMappie<ByteInput, LongOutput>() {
                            override fun map(from: ByteInput) = mapping {
                                to::value fromProperty from::value via ByteToLongMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Byte = 5

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, LongOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(LongOutput(input.toLong()))
        }
    }
}