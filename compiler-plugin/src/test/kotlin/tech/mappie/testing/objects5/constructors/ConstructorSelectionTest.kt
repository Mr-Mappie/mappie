package tech.mappie.testing.objects5.constructors

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
                import tech.mappie.api.ObjectMappie5
                import tech.mappie.testing.objects5.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie5<Input, Int, Int, Int, Int, Output>() {
                    override fun map(first: Input, second: Int, third: Int, fourth: Int, fifth: Int) = mapping(::Output)
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie5<Input, Int, Int, Int, Int, Output>()

            assertThat(mapper.map(Input(1), 2, 3, 4, 5))
                .isEqualTo(Output("constant"))
        }
    }

    @Test
    fun `explicitly call constructor with two parameters without mapping should fail`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie5
                import tech.mappie.testing.objects5.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie5<Input, Int, Int, Int, Int, Output>() {
                    override fun map(first: Input, second: Int, third: Int, fourth: Int, fifth: Int) =
                        mapping<String, String>(::Output)
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(5, "Target Output::first automatically resolved parameter first but cannot assign source type Input to target type String")
        }
    }

    @Test
    fun `explicitly call constructor with two parameters should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie5
                import tech.mappie.testing.objects5.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie5<Input, Int, Int, Int, Int, Output>() {
                    override fun map(first: Input, second: Int, third: Int, fourth: Int, fifth: Int) =
                        mapping<String, String>(::Output) {
                            to("first") fromValue third.toString()
                            to("second") fromValue fifth.toString()
                        }
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie5<Input, Int, Int, Int, Int, Output>()

            assertThat(mapper.map(Input(1), 2, 3, 4, 5))
                .isEqualTo(Output("35"))
        }
    }

    @Test
    fun `explicitly call constructor with three parameters of different type should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie5
                import tech.mappie.testing.objects5.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie5<Input, Int, Int, Int, Int, Output>() {
                    override fun map(first: Input, second: Int, third: Int, fourth: Int, fifth: Int) =
                         mapping<String, Int, Int>(::Output) {
                            to("first") fromValue first.input.toString()
                         }
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie5<Input, Int, Int, Int, Int, Output>()

            assertThat(mapper.map(Input(1), 2, 3, 4, 5))
                .isEqualTo(Output("15"))
        }
    }


    @Test
    fun `explicitly call constructor with two parameters of different type should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie5
                import tech.mappie.testing.objects5.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie5<Input, Int, Int, Int, Int, Output>() {
                    override fun map(first: Input, second: Int, third: Int, fourth: Int, fifth: Int) =
                        mapping<Int, Int>(::Output) {
                            to("first") fromValue 1
                        }
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie5<Input, Int, Int, Int, Int, Output>()

            assertThat(mapper.map(Input(1), 2, 3, 4, 5))
                .isEqualTo(Output("3"))
        }
    }

    @Test
    fun `multiple mapping calls of different constructors should fail`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie5
                import tech.mappie.testing.objects5.constructors.ConstructorSelectionTest.*

                class Mapper : ObjectMappie5<Input, Int, Int, Int, Int, Output>() {
                    override fun map(first: Input, second: Int, third: Int, fourth: Int, fifth: Int): Output {
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