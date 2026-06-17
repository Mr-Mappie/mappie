package tech.mappie.testing.twoway

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class SimpleTwoWayMapperTest : MappieTestCase() {

    data class IntWrapper(val value: Int)
    data class StringWrapper(val value: String)

    @Test
    fun `map property fromValue should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.TwoWayObjectMappie
                import tech.mappie.testing.twoway.SimpleTwoWayMapperTest.*

                class Mapper : TwoWayObjectMappie<IntWrapper, StringWrapper>() {
                    override fun map(from: IntWrapper) = mapping {
                        to::value fromProperty from::value transform Int::toString
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = twoWayObjectMappie<IntWrapper, StringWrapper>()

            assertThat(mapper.map(IntWrapper(1))).isEqualTo(StringWrapper("1"))
        }
    }
}