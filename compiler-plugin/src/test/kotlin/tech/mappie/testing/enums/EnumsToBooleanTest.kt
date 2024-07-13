package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadEnumMappieClass
import java.io.File
import kotlin.test.Test

class EnumsToBooleanTest {

    enum class Input { TRUE, FALSE }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map identical enums should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.EnumMappie
                        import tech.mappie.testing.enums.EnumsToBooleanTest.*
    
                        class Mapper : EnumMappie<Input, Boolean>() {
                            override fun map(from: Input) = mapping {
                                true fromEnumEntry Input.TRUE
                                false fromEnumEntry Input.FALSE
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val mapper = classLoader
                .loadEnumMappieClass<Input, Boolean>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input.TRUE)).isEqualTo(true)
            assertThat(mapper.map(Input.FALSE)).isEqualTo(false)
        }
    }
}
