package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ObjectWithNullableEnumToObjectWithNullableEnumWithSameEntriesTest : MappieTestCase() {
    data class Input(val text: InnerEnum?)
    @Suppress("unused") enum class InnerEnum { A, B, C; }

    data class Output(val text: OuterEnum?)
    @Suppress("unused") enum class OuterEnum { A, B, C; }

    @Test
    fun `map object with nested non-null enum with generated mapper should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.ObjectWithNullableEnumToObjectWithNullableEnumWithSameEntriesTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerEnum.A)))
                .isEqualTo(Output(OuterEnum.A))

            assertThat(mapper.map(Input(null)))
                .isEqualTo(Output(null))
        }
    }

    @Test
    fun `map object with nested non-null enum with explicit mapper and implicit mappings should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.ObjectWithNullableEnumToObjectWithNullableEnumWithSameEntriesTest.*

                class Mapper : ObjectMappie<Input, Output>()

                object InnerMapper : EnumMappie<InnerEnum, OuterEnum>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerEnum.A)))
                .isEqualTo(Output(OuterEnum.A))

            assertThat(mapper.map(Input(null)))
                .isEqualTo(Output(null))
        }
    }
}