package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadEnumMappieClass
import java.io.File
import kotlin.test.Test

class EnumToExpressionTest {

    enum class Input { TRUE, FALSE }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map enum to expression should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToExpressionTest.*

                class Mapper : EnumMappie<Input, Boolean>() {
                    override fun map(from: Input) = mapping {
                        true fromEnumEntry Input.TRUE
                        false fromEnumEntry Input.FALSE
                    }
                }
                """
            )
        } satisfies  {
            isOk()
            hasNoMessages()

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
