package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ObjectWithStringPropertyToObjectWithIntPropertyTest : MappieTestCase() {

    data class Input(val value: String)
    data class Output(val value: Int)

    @Test
    fun `explicit mapping using fromProperty with wrong type should fail`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithStringPropertyToObjectWithIntPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::value fromProperty from::value
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6, "Target Output::value of type Int cannot be assigned from from::value of type String")
        }
    }

    @Test
    fun `implicit mapping using fromProperty with wrong type should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithStringPropertyToObjectWithIntPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(
                4,
                "Target Output::value automatically resolved from Input::value but cannot assign source type String to target type Int"
            )
        }
    }

    @Test
    fun `explicit mapping using fromValue with wrong type should fail`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithStringPropertyToObjectWithIntPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::value fromValue from.value
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6, "Target Output::value of type Int cannot be assigned from value of type String")
        }
    }

    @Test
    fun `explicit mapping using fromExpression with wrong type should fail`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithStringPropertyToObjectWithIntPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::value fromExpression { it.value }
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6, "Target Output::value of type Int cannot be assigned from expression of type String")
        }
    }
}