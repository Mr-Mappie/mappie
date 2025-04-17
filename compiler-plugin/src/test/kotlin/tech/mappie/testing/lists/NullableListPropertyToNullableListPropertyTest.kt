package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

class NullableListPropertyToNullableListPropertyTest {
    data class Input(val text: List<LocalDateTime>?)

    data class Output(val text: List<LocalDate>?)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map nested nullable list to nullable list explicit with transform should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.NullableListPropertyToNullableListPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(listOf(LocalDateTime.MIN, LocalDateTime.MAX))))
                .isEqualTo(Output(listOf(LocalDate.MIN, LocalDate.MAX)))

            assertThat(mapper.map(Input(null)))
                .isEqualTo(Output(null))
        }
    }
}