package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ObjectWithNestedEnumTest {
    data class Input(val text: InnerEnum)
    enum class InnerEnum { A, B, C; }
    data class Output(val text: OuterEnum)
    enum class OuterEnum(val value: String) { A("A"), B("B"), C("C"); }

    @TempDir
    lateinit var directory: File

    @Test
    @Disabled
    fun `map data classes with nested enum using object InnerMapper without declaring nested mapping should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.EnumMappie
                        import tech.mappie.testing.enums.ObjectWithNestedEnumTest.*
    
                        class Mapper : ObjectMappie<Input, Output>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(InnerEnum.A)))
                .isEqualTo(Output(OuterEnum.A))
        }
    }

    @Test
    fun `map data classes with nested enum using object InnerMapper without declaring mapping should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.EnumMappie
                        import tech.mappie.testing.enums.ObjectWithNestedEnumTest.*
    
                        class Mapper : ObjectMappie<Input, Output>()

                        object InnerMapper : EnumMappie<InnerEnum, OuterEnum>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(InnerEnum.A)))
                .isEqualTo(Output(OuterEnum.A))
        }
    }
}