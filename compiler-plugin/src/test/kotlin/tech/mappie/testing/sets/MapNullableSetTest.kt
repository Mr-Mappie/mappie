package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

class MapNullableSetTest {

    data class Input(val value: LocalDateTime)

    data class Output(val value: LocalDate)

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapNullableSet implicit succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.MapNullableSetTest.*

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

            assertThat(mapper.mapNullableSet(setOf(Input(LocalDateTime.MIN), Input(LocalDateTime.MAX))))
                .isEqualTo(setOf(Output(LocalDate.MIN), Output(LocalDate.MAX)))

            assertThat(mapper.mapNullableSet(null))
                .isEqualTo(null)
        }
    }
}