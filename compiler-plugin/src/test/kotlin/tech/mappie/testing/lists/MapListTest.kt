package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

class MapListTest {

    data class Input(val value: LocalDateTime)

    data class Output(val value: LocalDate)

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapListNullable implicit succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MapListTest.*

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

            assertThat(mapper.mapList(listOf(Input(LocalDateTime.MIN), Input(LocalDateTime.MAX))))
                .isEqualTo(listOf(Output(LocalDate.MIN), Output(LocalDate.MAX)))
        }
    }
}