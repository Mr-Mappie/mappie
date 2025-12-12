package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class LocalConversionMethodTest : MappieTestCase() {

    data class StringWrapper(val value: String)

    data class Input(val name: StringWrapper)

    data class Output(val name: String)

    data class IntWrapper(val value: Int)

    data class InputInt(val count: IntWrapper)

    data class OutputInt(val count: Int)

    data class ListInput(val items: List<String>)

    data class SetOutput(val items: Set<String>)

    @Test
    fun `local conversion method is auto-discovered`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.LocalConversionMethodTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    fun unwrap(wrapper: StringWrapper): String = wrapper.value

                    override fun map(from: Input) = mapping()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input(StringWrapper("test")))).isEqualTo(Output("test"))
        }
    }

    @Test
    fun `local conversion method with different name works`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.LocalConversionMethodTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    fun extractValue(wrapper: StringWrapper): String = wrapper.value

                    override fun map(from: Input) = mapping()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input(StringWrapper("hello")))).isEqualTo(Output("hello"))
        }
    }

    @Test
    fun `multiple local conversion methods work for different types`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.LocalConversionMethodTest.*

                data class CombinedInput(val name: StringWrapper, val count: IntWrapper)
                data class CombinedOutput(val name: String, val count: Int)

                class Mapper : ObjectMappie<CombinedInput, CombinedOutput>() {
                    fun unwrapString(wrapper: StringWrapper): String = wrapper.value
                    fun unwrapInt(wrapper: IntWrapper): Int = wrapper.value

                    override fun map(from: CombinedInput) = mapping()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()
        }
    }

    @Test
    fun `conversion method from interface is discovered`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.LocalConversionMethodTest.*

                interface CommonConverters {
                    fun unwrap(wrapper: StringWrapper): String = wrapper.value
                }

                class Mapper : ObjectMappie<Input, Output>(), CommonConverters {
                    override fun map(from: Input) = mapping()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input(StringWrapper("from-interface")))).isEqualTo(Output("from-interface"))
        }
    }

    @Test
    fun `local method takes precedence over interface method`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.LocalConversionMethodTest.*

                interface CommonConverters {
                    fun unwrap(wrapper: StringWrapper): String = wrapper.value
                }

                class Mapper : ObjectMappie<Input, Output>(), CommonConverters {
                    // Override with different behavior
                    override fun unwrap(wrapper: StringWrapper): String = wrapper.value.uppercase()

                    override fun map(from: Input) = mapping()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input(StringWrapper("test")))).isEqualTo(Output("TEST"))
        }
    }

    @Test
    fun `generic conversion method works`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.LocalConversionMethodTest.*

                class Mapper : ObjectMappie<ListInput, SetOutput>() {
                    fun <T> toSet(list: List<T>): Set<T> = list.toSet()

                    override fun map(from: ListInput) = mapping()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<ListInput, SetOutput>()
            val result = mapper.map(ListInput(listOf("a", "b", "a")))
            assertThat(result.items).containsExactlyInAnyOrder("a", "b")
        }
    }

    @Test
    fun `map function is not treated as conversion method`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.LocalConversionMethodTest.*

                data class OtherInput(val value: String)
                data class OtherOutput(val value: Int)

                class Mapper : ObjectMappie<OtherInput, OtherOutput>() {
                    // This should not be used as a conversion method because return type differs
                    override fun map(from: OtherInput) = mapping {
                        OtherOutput::value fromProperty from::value transform { it.toInt() }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()
        }
    }

    @Test
    fun `local conversion method works with object mapper`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.LocalConversionMethodTest.*

                object Mapper : ObjectMappie<Input, Output>() {
                    fun unwrap(wrapper: StringWrapper): String = wrapper.value

                    override fun map(from: Input) = mapping()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()
        }
    }

    @Test
    fun `conversion method with complex transformation works`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.LocalConversionMethodTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    fun processWrapper(wrapper: StringWrapper): String {
                        return wrapper.value.trim().lowercase()
                    }

                    override fun map(from: Input) = mapping()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input(StringWrapper("  HELLO WORLD  ")))).isEqualTo(Output("hello world"))
        }
    }

    @Test
    fun `conversion methods from multiple interfaces work`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.LocalConversionMethodTest.*

                data class MultiInput(val name: StringWrapper, val count: IntWrapper)
                data class MultiOutput(val name: String, val count: Int)

                interface StringConverters {
                    fun unwrapString(wrapper: StringWrapper): String = wrapper.value
                }

                interface IntConverters {
                    fun unwrapInt(wrapper: IntWrapper): Int = wrapper.value
                }

                class Mapper : ObjectMappie<MultiInput, MultiOutput>(), StringConverters, IntConverters {
                    override fun map(from: MultiInput) = mapping()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()
        }
    }
}
