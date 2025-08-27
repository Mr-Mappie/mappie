package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

class FromExpressionFlexibleNullableTest {

    data class Input(val value: LocalDateTime?)
    data class Output(val value: LocalDateTime)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map property fromExpression FlexibleNullable should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import java.time.LocalDateTime
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.FromExpressionFlexibleNullableTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::value fromExpression { it.value ?: LocalDateTime.MIN }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasWarningMessage(
                7,
                "Target Output::value of type Output is unsafe to be assigned from expression of platform type LocalDateTime?"
            )

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(null))).isEqualTo(Output(LocalDateTime.MIN))
        }
    }
}