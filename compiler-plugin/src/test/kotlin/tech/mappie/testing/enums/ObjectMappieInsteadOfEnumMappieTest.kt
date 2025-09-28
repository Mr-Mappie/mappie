package tech.mappie.testing.enums

import tech.mappie.testing.MappieTestCase
import kotlin.test.Test

class ObjectMappieInsteadOfEnumMappieTest : MappieTestCase() {

    data class InputObject(val boolean: Boolean)
    enum class Input { TRUE, FALSE }

    enum class Output { TRUE, FALSE }

    @Test
    fun `map enum to enum in ObjectMappie should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.ObjectMappieInsteadOfEnumMappieTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies  {
            isCompilationError()
            hasErrorMessage(4, "Target type Output cannot be an enum class", listOf("Override EnumMappie instead of ObjectMappie"))
        }
    }

    @Test
    fun `map object to enum explicitly in ObjectMappie should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.ObjectMappieInsteadOfEnumMappieTest.*

                class Mapper : ObjectMappie<InputObject, Output>() {
                    override fun map(from: InputObject) = 
                        if (from.boolean) Output.TRUE else Output.FALSE
                }
                """
            )
        } satisfies {
            isOk()
        }
    }

    @Test
    fun `map object to enum implicitly in ObjectMappie should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.ObjectMappieInsteadOfEnumMappieTest.*

                class Mapper : ObjectMappie<InputObject, Output>()
                """
            )
        } satisfies  {
            isCompilationError()
            hasErrorMessage(4, "Target type Output cannot be an enum class", listOf("Override EnumMappie instead of ObjectMappie"))
        }
    }
}
