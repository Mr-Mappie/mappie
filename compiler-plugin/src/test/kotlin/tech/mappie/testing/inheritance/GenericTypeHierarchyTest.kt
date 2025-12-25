package tech.mappie.testing.inheritance

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GenericTypeHierarchyTest : MappieTestCase() {

    enum class Input { A, B }
    enum class Output { A_, B_ }

    @Test
    fun `multiple generics in class hierarchy`() {
        compile(verbose = true) {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.inheritance.GenericTypeHierarchyTest.*
                import kotlin.enums.EnumEntries
                
                class Mapper : IntermediateMapper<Input, Output>() {
                    override val entries = Output.entries
                }
                abstract class IntermediateMapper<A : Enum<A>, B : Enum<B>> : BaseMapper<A, B>()
                abstract class BaseMapper<C : Enum<C>, D : Enum<D>> : EnumMappie<C, D>() {
                
                    abstract val entries: EnumEntries<D>
                
                    override fun map(from: C) : D {
                        return entries.first { it.name == from.name + "_" }
                    } 
                }
                """.trimIndent()
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = enumMappie<Input, Output>()

            assertThat(mapper.map(Input.A)).isEqualTo(Output.A_)
            assertThat(mapper.map(Input.B)).isEqualTo(Output.B_)
        }
    }

    @Test
    fun `multiple generics reversed in class hierarchy`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.inheritance.GenericTypeHierarchyTest.*
                import kotlin.enums.EnumEntries
                
                class Mapper : IntermediateMapper<Input, Output>() {
                    override val entries = Output.entries
                }
                abstract class IntermediateMapper<B : Enum<B>, A : Enum<A>> : BaseMapper<A, B>()
                abstract class BaseMapper<D : Enum<D>, C : Enum<C>> : EnumMappie<C, D>() {
                
                    abstract val entries: EnumEntries<D>
                
                    override fun map(from: C) : D {
                        return entries.first { it.name == from.name + "_" }
                    } 
                }
                """.trimIndent()
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = enumMappie<Input, Output>()

            assertThat(mapper.map(Input.A)).isEqualTo(Output.A_)
            assertThat(mapper.map(Input.B)).isEqualTo(Output.B_)
        }
    }
}