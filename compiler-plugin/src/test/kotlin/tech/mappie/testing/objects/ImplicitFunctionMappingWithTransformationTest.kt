package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ImplicitFunctionMappingWithTransformationTest : MappieTestCase() {

    class Input { @Suppress("unused") fun getInner() = InnerInput("mappie") }
    data class InnerInput(val value: String)

    data class Output(val inner: InnerOutput)
    data class InnerOutput(val value: String)

    @Test
    fun `implicit getter mapping uses transformation`() {
        compile {
            file(
                "Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ImplicitFunctionMappingWithTransformationTest.*

                class Mapper : ObjectMappie<Input, Output>()

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>() {
                    override fun map(from: InnerInput) = mapping {
                        InnerOutput::value fromProperty from::value transform { it.uppercase() }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input()).inner.value)
                .isEqualTo("MAPPIE")
        }
    }
}