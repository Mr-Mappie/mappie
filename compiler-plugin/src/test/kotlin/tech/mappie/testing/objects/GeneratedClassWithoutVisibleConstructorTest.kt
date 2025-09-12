package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.time.LocalDate
import java.time.OffsetDateTime

class GeneratedClassWithoutVisibleConstructorTest {

    data class Input(val value: LocalDate?)
    data class Output(val value: OffsetDateTime?)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map nullable implicit should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedClassWithoutVisibleConstructorTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(4, "No implicit mapping can be generated from LocalDate? to OffsetDateTime?",
                listOf("Target class has no accessible constructor")
            )
        }
    }

    @Test
    fun `map nullable explicit from null should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedClassWithoutVisibleConstructorTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::value fromValue null
                    }
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(LocalDate.now())))
                .isEqualTo(Output(null))

        }
    }
}