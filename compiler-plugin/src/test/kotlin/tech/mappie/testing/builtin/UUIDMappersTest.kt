package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.util.UUID

class UUIDMappersTest {

    @TempDir
    lateinit var directory: File

    data class UUIDInput(val value: UUID)

    data class StringOutput(val value: String)

    @Test
    fun `map UUID to String implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.UUIDMappersTest.*

                        class Mapper : ObjectMappie<UUIDInput, StringOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = UUID.fromString("749c9041-ce3b-416b-aec7-3be7edf52de9")

            val mapper = classLoader
                .loadObjectMappieClass<UUIDInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(UUIDInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map UUID to String explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.UUIDMappersTest.*

                        class Mapper : ObjectMappie<UUIDInput, StringOutput>() {
                            override fun map(from: UUIDInput) = mapping {
                                to::value fromProperty from::value via UUIDToStringMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = UUID.fromString("8cad0e3d-31d1-4d03-9314-a5e3f3b557b4")

            val mapper = classLoader
                .loadObjectMappieClass<UUIDInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(UUIDInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}
