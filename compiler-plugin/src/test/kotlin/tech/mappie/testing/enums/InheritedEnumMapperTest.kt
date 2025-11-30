package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import tech.mappie.testing.MappieTestCase
import kotlin.test.Test

class InheritedEnumMapperTest : MappieTestCase() {

    enum class InputA { A, B }
    enum class InputB { A, B, C }
    enum class Output { a, b, c }

    @Test
    fun `multiple concrete mappers implement abstract mapper`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.InheritedEnumMapperTest.*
                import kotlin.enums.EnumEntries

                abstract class BaseMapper<E : Enum<E>, T : Enum<T>>(
                    private val entries: EnumEntries<T>
                ) : EnumMappie<E, T>() {
                    
                    override fun map(from: E): T = 
                        entries.first { it.name == from.name.lowercase() }
                }

                class InputAMapper : BaseMapper<InputA, Output>(Output.entries)
                class InputBMapper : BaseMapper<InputB, Output>(Output.entries)
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            val mapperA = enumMappie<InputA, Output>("InputAMapper")

            assertThat(mapperA.map(InputA.A)).isEqualTo(Output.a)
            assertThat(mapperA.map(InputA.B)).isEqualTo(Output.b)

            val mapperB = enumMappie<InputB, Output>("InputBMapper")

            assertThat(mapperB.map(InputB.A)).isEqualTo(Output.a)
            assertThat(mapperB.map(InputB.B)).isEqualTo(Output.b)
            assertThat(mapperB.map(InputB.C)).isEqualTo(Output.c)
        }
    }

    @Test
    fun `multiple concrete mappers implement two layered abstract mapper`() {
        compile(verbose = true) {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.InheritedEnumMapperTest.*
                import kotlin.enums.EnumEntries

                abstract class BaseMapper<E : Enum<E>, T : Enum<T>>(
                    private val entries: EnumEntries<T>
                ) : EnumMappie<E, T>() {
                    
                    override fun map(from: E): T = 
                        entries.first { it.name == from.name.lowercase() }
                }

                abstract class IntermediateMapper<E : Enum<E>, T : Enum<T>>(
                    entries: EnumEntries<T>
                ) : BaseMapper<E, T>(entries) {
                    
                    override fun map(from: E): T = super.map(from)
                }

                class InputAMapper : IntermediateMapper<InputA, Output>(Output.entries)
                class InputBMapper : IntermediateMapper<InputB, Output>(Output.entries)
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            val mapperA = enumMappie<InputA, Output>("InputAMapper")

            assertThat(mapperA.map(InputA.A)).isEqualTo(Output.a)
            assertThat(mapperA.map(InputA.B)).isEqualTo(Output.b)

            val mapperB = enumMappie<InputB, Output>("InputBMapper")

            assertThat(mapperB.map(InputB.A)).isEqualTo(Output.a)
            assertThat(mapperB.map(InputB.B)).isEqualTo(Output.b)
            assertThat(mapperB.map(InputB.C)).isEqualTo(Output.c)
        }
    }

    @Test
    fun `concrete mapper implement abstract base mapper to form complete mapping`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.InheritedEnumMapperTest.*

                abstract class BaseMapper : EnumMappie<InputA, Output>() {
                    override fun map(from: InputA) = mapping {
                        Output.a fromEnumEntry InputA.A
                    } 
                }

                class FirstMapper : BaseMapper() {
                    override fun map(from: InputA) = mapping {
                        Output.b fromEnumEntry InputA.B
                    } 
                }

                class SecondMapper : BaseMapper() {
                    override fun map(from: InputA) = mapping {
                        Output.c fromEnumEntry InputA.B
                    }
                }
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            val firstMapper = enumMappie<InputA, Output>("FirstMapper")

            assertThat(firstMapper.map(InputA.A)).isEqualTo(Output.a)
            assertThat(firstMapper.map(InputA.B)).isEqualTo(Output.b)

            val secondMapper = enumMappie<InputA, Output>("SecondMapper")

            assertThat(secondMapper.map(InputA.A)).isEqualTo(Output.a)
            assertThat(secondMapper.map(InputA.B)).isEqualTo(Output.c)
        }
    }

    @Test
    fun `concrete mapper implement abstract base mapper to form duplicate mapping`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.InheritedEnumMapperTest.*

                abstract class BaseMapper : EnumMappie<InputA, Output>() {
                    override fun map(from: InputA) = mapping {
                        Output.a fromEnumEntry InputA.A
                    } 
                }

                class FirstMapper : BaseMapper() {
                    override fun map(from: InputA) = mapping {
                        Output.c fromEnumEntry InputA.A
                        Output.b fromEnumEntry InputA.B
                    } 
                }

                class SecondMapper : BaseMapper() {
                    override fun map(from: InputA) = mapping {
                        Output.c fromEnumEntry InputA.B
                    }
                }
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            val firstMapper = enumMappie<InputA, Output>("FirstMapper")

            assertThat(firstMapper.map(InputA.A)).isEqualTo(Output.c)
            assertThat(firstMapper.map(InputA.B)).isEqualTo(Output.b)

            val secondMapper = enumMappie<InputA, Output>("SecondMapper")

            assertThat(secondMapper.map(InputA.A)).isEqualTo(Output.a)
            assertThat(secondMapper.map(InputA.B)).isEqualTo(Output.c)
        }
    }
}
