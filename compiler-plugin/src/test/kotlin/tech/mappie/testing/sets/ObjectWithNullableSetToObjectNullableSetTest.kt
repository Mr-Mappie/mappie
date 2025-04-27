package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

class ObjectWithNullableSetToObjectNullableSetTest {
    data class Input(val text: Set<LocalDateTime>?)

    data class Output(val text: Set<LocalDate>?)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map nested nullable set to nullable set explicit with transform should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.ObjectWithNullableSetToObjectNullableSetTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(setOf(LocalDateTime.MIN, LocalDateTime.MAX))))
                .isEqualTo(Output(setOf(LocalDate.MIN, LocalDate.MAX)))

            assertThat(mapper.map(Input(null)))
                .isEqualTo(Output(null))
        }
    }
}