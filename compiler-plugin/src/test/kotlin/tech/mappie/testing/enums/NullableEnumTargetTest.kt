package tech.mappie.testing.enums

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class NullableEnumTargetTest : MappieTestCase() {

    data class Input(val value: InputEnum)
    data class Output(val value: OutputEnum?)

    enum class InputEnum { A, B, C }
    enum class OutputEnum { A, B }

    @Test
    fun `map to nullable enum target with implicit mapping should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.NullableEnumTargetTest.*

                class Mapper : ObjectMappie<Input, Output>()

                object EnumMapper : EnumMappie<InputEnum, OutputEnum?>() {
                    override fun map(from: InputEnum) = mapping {
                        null fromEnumEntry InputEnum.C
                    }
                }
                """
            )
        } satisfies {
            isOk()
        }
    }

    @Test
    fun `map to nullable enum target with implicit mapper reference should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.NullableEnumTargetTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::value fromProperty from::value
                    }
                }

                object EnumMapper : EnumMappie<InputEnum, OutputEnum?>() {
                    override fun map(from: InputEnum) = mapping {
                        null fromEnumEntry InputEnum.C
                    }
                }
                """
            )
        } satisfies {
            isOk()
        }
    }

    @Test
    fun `map to nullable enum target with explicit mapper reference should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.NullableEnumTargetTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::value fromProperty from::value via EnumMapper
                    }
                }

                object EnumMapper : EnumMappie<InputEnum, OutputEnum?>() {
                    override fun map(from: InputEnum) = mapping {
                        null fromEnumEntry InputEnum.C
                    }
                }
                """
            )
        } satisfies {
            isOk()
        }
    }
}