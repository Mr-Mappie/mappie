package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ObjectWithEnumToObjectWithEnumWithSameEntriesTest {
    data class Input(val text: InnerEnum)
    @Suppress("unused") enum class InnerEnum { A, B, C; }

    data class Output(val text: OuterEnum)
    @Suppress("unused") enum class OuterEnum(val value: String) { A("A"), B("B"), C("C"); }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with nested enum with generated mapper should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.ObjectWithEnumToObjectWithEnumWithSameEntriesTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(InnerEnum.A)))
                .isEqualTo(Output(OuterEnum.A))
        }
    }

    @Test
    fun `map object with nested enum with explicit mapper and implicit mappings should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.ObjectWithEnumToObjectWithEnumWithSameEntriesTest.*

                class Mapper : ObjectMappie<Input, Output>()

                object InnerMapper : EnumMappie<InnerEnum, OuterEnum>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(InnerEnum.A)))
                .isEqualTo(Output(OuterEnum.A))
        }
    }
}