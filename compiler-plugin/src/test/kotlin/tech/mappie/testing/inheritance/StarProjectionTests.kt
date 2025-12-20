package tech.mappie.testing.inheritance

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class StarProjectionTests : MappieTestCase() {

    enum class InputEnum { FOO, BAR, FOO_BAR }
    data class InputWrapperSame(val id: String, val elements: Set<InputEnum>)
    data class InputWrapperDiff(val id: String, val inputElements: Set<InputEnum>)
    data class InputWrapperNested(val elements: List<Set<String>>)

    enum class OutputEnum { Foo, Bar, FooBar }
    data class OutputWrapperSame(val id: String, val elements: Set<OutputEnum>)
    data class OutputWrapperDiff(val id: String, val outputElements: Set<OutputEnum>)
    data class OutputWrapperNested(val elements: List<Set<String>>)

    /*
        Without the star projection change:
        This throws the star projection issue.

        With the star projection change:
        This doesn't work although I would assume it should since the property names of the wrappers are
        the same and there is an enum mappie defined for the enum but it can't find it.
    */
    @Test
    fun `map with implicit enum mapping same name`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.inheritance.StarProjectionTests.*
                import kotlin.enums.EnumEntries

                class Mapper : ObjectMappie<InputWrapperSame, OutputWrapperSame>()

                object TestEnumMapper: BaseEnumMapper<InputEnum, OutputEnum>(OutputEnum.entries)

                abstract class BaseEnumMapper<F: Enum<F>, T: Enum<T>>(
                    private val targetValues: EnumEntries<T>,
                ): EnumMappie<F, T>() {
                    override fun map(from: F): T {
                        val sourceCleaned = from.name.lowercase()
                            .replace(
                                "_",
                                ""
                            )
                        return targetValues.first { target ->
                            target.name.lowercase()
                                .replace(
                                    "_",
                                    ""
                                ) == sourceCleaned
                        }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<InputWrapperSame, OutputWrapperSame>()

            val input = InputWrapperSame(
                id = "Test",
                elements = setOf(InputEnum.FOO, InputEnum.FOO_BAR)
            )
            val output = OutputWrapperSame(
                id = "Test",
                elements = setOf(OutputEnum.Foo, OutputEnum.FooBar)
            )

            assertThat(mapper.map(input))
                .isEqualTo(output)
        }
    }

    /*
        Without the star projection change:
        This throws the star projection issue.

        With the star projection change:
        This doesn't work although I would assume it should. The property names of the wrappers are
        different, but there is a name mapping defined, it just doesn't use the enum mapper below it.
    */
    @Test
    fun `map with implicit enum mapping different name`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.inheritance.StarProjectionTests.*
                import kotlin.enums.EnumEntries

                class Mapper : ObjectMappie<InputWrapperDiff, OutputWrapperDiff>() {
                    override fun map(from: InputWrapperDiff): OutputWrapperDiff = mapping {
                        to::outputElements fromProperty from::inputElements
                    }
                }

                object TestEnumMapper: BaseEnumMapper<InputEnum, OutputEnum>(OutputEnum.entries)

                abstract class BaseEnumMapper<F: Enum<F>, T: Enum<T>>(
                    private val targetValues: EnumEntries<T>,
                ): EnumMappie<F, T>() {
                    override fun map(from: F): T {
                        val sourceCleaned = from.name.lowercase()
                            .replace(
                                "_",
                                ""
                            )
                        return targetValues.first { target ->
                            target.name.lowercase()
                                .replace(
                                    "_",
                                    ""
                                ) == sourceCleaned
                        }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<InputWrapperDiff, OutputWrapperDiff>()

            val input = InputWrapperDiff(
                id = "Test",
                inputElements = setOf(InputEnum.FOO, InputEnum.FOO_BAR)
            )
            val output = OutputWrapperDiff(
                id = "Test",
                outputElements = setOf(OutputEnum.Foo, OutputEnum.FooBar)
            )

            assertThat(mapper.map(input))
                .isEqualTo(output)
        }
    }

    /*
        Without the star projection change:
        Same as below

        With the star projection change:
        This is the same test case as the previous one with different property names, but this test passes
        because it doesn't use inheritance for the enum mappers. This means enum mappies provided by
        inheritance are not accounted for in the implicit mapper resolution.
    */
    @Test
    fun `map with implicit enum mapping different name without inheritance`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.inheritance.StarProjectionTests.*
                import kotlin.enums.EnumEntries

                class Mapper : ObjectMappie<InputWrapperDiff, OutputWrapperDiff>() {
                    override fun map(from: InputWrapperDiff): OutputWrapperDiff = mapping {
                        to::outputElements fromProperty from::inputElements
                    }
                }

                object TestEnumMapper: EnumMappie<InputEnum, OutputEnum>() {
                    override fun map(from: InputEnum): OutputEnum {
                        val sourceCleaned = from.name.lowercase()
                            .replace(
                                "_",
                                ""
                            )
                        return OutputEnum.entries.first { target ->
                            target.name.lowercase()
                                .replace(
                                    "_",
                                    ""
                                ) == sourceCleaned
                        }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<InputWrapperDiff, OutputWrapperDiff>()

            val input = InputWrapperDiff(
                id = "Test",
                inputElements = setOf(InputEnum.FOO, InputEnum.FOO_BAR)
            )
            val output = OutputWrapperDiff(
                id = "Test",
                outputElements = setOf(OutputEnum.Foo, OutputEnum.FooBar)
            )

            assertThat(mapper.map(input))
                .isEqualTo(output)
        }
    }

    /*
        Without the star projection change:
        Same as below

        With the star projection change:
        This works because the mapping is done by an expression and doesn't even use the newly added
        mapper for immutable sets.
    */
    @Test
    fun `map with explicit enum mapping different name`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.inheritance.StarProjectionTests.*
                import kotlin.enums.EnumEntries
                import kotlinx.collections.immutable.toImmutableSet

                class Mapper : ObjectMappie<InputWrapperDiff, OutputWrapperDiff>() {
                    override fun map(from: InputWrapperDiff): OutputWrapperDiff = mapping {
                        to::outputElements fromExpression { from.inputElements.map { TestEnumMapper.map(it)}.toImmutableSet() }
                    }
                }

                object TestEnumMapper: BaseEnumMapper<InputEnum, OutputEnum>(OutputEnum.entries)

                abstract class BaseEnumMapper<F: Enum<F>, T: Enum<T>>(
                    private val targetValues: EnumEntries<T>,
                ): EnumMappie<F, T>() {
                    override fun map(from: F): T {
                        val sourceCleaned = from.name.lowercase()
                            .replace(
                                "_",
                                ""
                            )
                        return targetValues.first { target ->
                            target.name.lowercase()
                                .replace(
                                    "_",
                                    ""
                                ) == sourceCleaned
                        }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<InputWrapperDiff, OutputWrapperDiff>()

            val input = InputWrapperDiff(
                id = "Test",
                inputElements = setOf(InputEnum.FOO, InputEnum.FOO_BAR)
            )
            val output = OutputWrapperDiff(
                id = "Test",
                outputElements = setOf(OutputEnum.Foo, OutputEnum.FooBar)
            )

            assertThat(mapper.map(input))
                .isEqualTo(output)
        }
    }

    /*
        Without the star projection change:
        Same as below

        With the star projection change:
        In this test, I tried to trigger the star projection compiler error, but I found a more interesting case.
        This test throws a StackOverflowError when compiling. It seems like nested collections are unsupported
        right now.
    */
    @Test
    fun `map nested implicit`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.inheritance.StarProjectionTests.*

                class Mapper : ObjectMappie<InputWrapperNested, OutputWrapperNested>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<InputWrapperNested, OutputWrapperNested>()

            val input = InputWrapperNested(listOf(setOf("FOO", "BAR"), setOf("FOO_BAR")))
            val output = OutputWrapperNested(listOf(setOf("FOO", "BAR"), setOf("FOO_BAR")))

            assertThat(mapper.map(input))
                .isEqualTo(output)
        }
    }
}