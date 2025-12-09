package tech.mappie.testing.objects.constructors

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ConstructorSelectionTest : MappieTestCase() {

    data class Input(val input: Int)
    data class Output(val output: String) {
        constructor() : this("constant")
        constructor(first: Int, second: Int) : this((first + second).toString())
        constructor(first: String, second: String) : this(first + second)
        constructor(first: String, second: Int, third: Int) : this(first + (second + third))
    }

    @Test
    fun `explicitly call constructor with zero parameters should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping(::Output)
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(1)))
                .isEqualTo(Output("constant"))
        }
    }

    @Test
    fun `explicitly call constructor with two parameters without mapping should fail`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping<String, String>(::Output)
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(5, "Target Output::first has no source defined")
            hasErrorMessage(5, "Target Output::second has no source defined")
        }
    }

    @Test
    fun `explicitly call constructor with two parameters should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping<String, String>(::Output) {
                        to("first") fromValue from.input.toString()
                        to("second") fromValue from.input.toString()
                    }
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(1)))
                .isEqualTo(Output("11"))
        }
    }

    @Test
    fun `explicitly call constructor with three parameters of different type should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping<String, Int, Int>(::Output) {
                        to("first") fromValue "test"
                        to("second") fromProperty from::input
                        to("third") fromProperty from::input
                    }
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(1)))
                .isEqualTo(Output("test2"))
        }
    }


    @Test
    fun `explicitly call constructor with two parameters of different type should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping<Int, Int>(::Output) {
                        to("first") fromValue 1
                        to("second") fromValue 2
                    }
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(1)))
                .isEqualTo(Output("3"))
        }
    }

    @Test
    fun `multiple mapping calls of different constructors should fail`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        val x = mapping<Int, Int>()
                        val y = mapping<String, String>(::Output)
                        return y
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6, "Multiple calls of the function 'mapping' while only one is allowed")
        }
    }
}