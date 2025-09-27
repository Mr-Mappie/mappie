package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class FromExpressionTest : MappieTestCase() {

    data class Output(val value: String)

    @Test
    fun `map property fromExpression should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.FromExpressionTest.*

                class Mapper : ObjectMappie<Unit, Output>() {
                    override fun map(from: Unit) = mapping {
                        Output::value fromExpression { it::class.simpleName!! }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Unit, Output>()

            assertThat(mapper.map(Unit)).isEqualTo(Output(Unit::class.simpleName!!))
        }
    }

    @Test
    fun `map property fromExpression should succeed with method reference`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.FromExpressionTest.*

                class Mapper : ObjectMappie<Int, Output>() {
                    override fun map(from: Int) = mapping {
                        Output::value fromExpression Int::toString
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Int, Output>()

            assertThat(mapper.map(101)).isEqualTo(Output("101"))
        }
    }

    @Test
    fun `map property fromExpression should fail with method reference with wrong return type`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.FromExpressionTest.*

                class Mapper : ObjectMappie<Int, Output>() {
                    override fun map(from: Int) = mapping {
                        Output::value fromExpression Int::toInt
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6, "Target Output::value of type String cannot be assigned from expression of type Int")
        }
    }
}