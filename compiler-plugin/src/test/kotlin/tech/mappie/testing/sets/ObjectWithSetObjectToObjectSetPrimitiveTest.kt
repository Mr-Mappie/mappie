package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ObjectWithSetObjectToObjectSetPrimitiveTest {
    data class Input(val text: Set<InnerInput>)
    data class InnerInput(val value: String)

    data class Output(val text: Set<String>)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map set without declaring via succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.ObjectWithSetObjectToObjectSetPrimitiveTest.*

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
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(setOf(InnerInput("A"), InnerInput("B")))))
                .isEqualTo(Output(setOf("A", "B")))
        }
    }

    @Test
    fun `map via forSet should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.ObjectWithSetObjectToObjectSetPrimitiveTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::text fromProperty from::text via InnerMapper.forSet
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, String>() {
                    override fun map(from: InnerInput) = from.value
                }
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

            assertThat(mapper.map(Input(setOf(InnerInput("A"), InnerInput("B")))))
                .isEqualTo(Output(setOf("A", "B")))
        }
    }
}