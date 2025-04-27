package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ListPropertyObjectToListPropertyPrimitiveTest {
    data class Input(val text: List<InnerInput>)
    data class InnerInput(val value: String)

    data class Output(val text: List<String>)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map list explicit with implicit via should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.ListPropertyObjectToListPropertyPrimitiveTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::text fromProperty from::text
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, String>() {
                    override fun map(from: InnerInput) = from.value
                }
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

            assertThat(mapper.map(Input(listOf(InnerInput("A"), InnerInput("B")))))
                .isEqualTo(Output(listOf("A", "B")))
        }
    }

    @Test
    fun `map via forList should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.ListPropertyObjectToListPropertyPrimitiveTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::text fromProperty from::text via InnerMapper.forList
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, String>() {
                    override fun map(from: InnerInput) = from.value
                }
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

            assertThat(mapper.map(Input(listOf(InnerInput("A"), InnerInput("B")))))
                .isEqualTo(Output(listOf("A", "B")))
        }
    }
}