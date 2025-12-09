package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InjectedMapperCanBeUsedTest : MappieTestCase() {

    data class Input(val text: String)
    data class Output(val text: String)

    @Test
    fun `mapper with constructor injected can be used in explicit via should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.InjectedMapperCanBeUsedTest.*

                class Mapper(private val mapper: CapitalizingMapper) : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::text fromProperty from::text via mapper
                    }
                }

                class CapitalizingMapper :ObjectMappie<String, String>() {
                    override fun map(from: String) = from.uppercase()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val injected = objectMappie<String, String>("CapitalizingMapper")
            val mapper = objectMappie<Input, Output>("Mapper", injected)

            assertThat(mapper.map(Input("test")))
                .isEqualTo(Output("TEST"))
        }
    }
}