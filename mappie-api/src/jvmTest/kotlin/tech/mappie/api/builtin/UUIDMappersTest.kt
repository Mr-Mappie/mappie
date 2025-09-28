package tech.mappie.api.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.util.UUID

class UUIDMappersTest : MappieTestCase() {

    data class UUIDInput(val value: UUID)

    data class StringOutput(val value: String)

    @Test
    fun `map UUID to String implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.UUIDMappersTest.*

                class Mapper : ObjectMappie<UUIDInput, StringOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = UUID.fromString("749c9041-ce3b-416b-aec7-3be7edf52de9")

            val mapper = objectMappie<UUIDInput, StringOutput>()

            assertThat(mapper.map(UUIDInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map UUID to String explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.UUIDMappersTest.*

                class Mapper : ObjectMappie<UUIDInput, StringOutput>() {
                    override fun map(from: UUIDInput) = mapping {
                        to::value fromProperty from::value via UUIDToStringMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = UUID.fromString("8cad0e3d-31d1-4d03-9314-a5e3f3b557b4")

            val mapper = objectMappie<UUIDInput, StringOutput>()

            assertThat(mapper.map(UUIDInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}