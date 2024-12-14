package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

class MapSetTest {

    data class Input(val value: LocalDateTime)

    data class Output(val value: LocalDate)

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapSet implicit succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.MapSetTest.*

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

            assertThat(mapper.mapSet(setOf(Input(LocalDateTime.MIN), Input(LocalDateTime.MAX))))
                .isEqualTo(setOf(Output(LocalDate.MIN), Output(LocalDate.MAX)))
        }
    }
}