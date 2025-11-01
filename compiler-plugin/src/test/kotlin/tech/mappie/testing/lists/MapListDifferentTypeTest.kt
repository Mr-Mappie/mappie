package tech.mappie.testing.lists

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class MapListDifferentTypeTest : MappieTestCase() {

    data class Input(val value: List<String>)

    data class Output(val value: List<Int>)

    @Test
    fun `map list with different type should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MapListDifferentTypeTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(4, "No implicit mapping can be generated from String to Int")
        }
    }

    @Test
    fun `map list with different type explicit should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.collections.*
                import tech.mappie.testing.lists.MapListDifferentTypeTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::value fromProperty from::value via IterableToListMapper(InnerMapper)
                    }
                }
                
                object InnerMapper : ObjectMappie<String, String>() {
                    override fun map(from: String): String = from
                }

                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(7, "Argument type mismatch: actual type is 'IterableToListMapper<String, String>', but 'Mappie<List<Int>>' was expected.")
        }
    }
}