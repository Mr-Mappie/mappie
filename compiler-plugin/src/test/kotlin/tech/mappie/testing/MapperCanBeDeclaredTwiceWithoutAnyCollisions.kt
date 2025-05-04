package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File

class MapperCanBeDeclaredTwiceWithoutAnyCollisions {

    data class FirstInput(val nested: NestedInput)
    data class SecondInput(val nested: NestedInput)
    @Suppress("unused") enum class NestedInput { A, B, C }

    data class FirstOutput(val nested: NestedOutput)
    data class SecondOutput(val nested: NestedOutput)
    @Suppress("unused") enum class NestedOutput { A, B, C }

    @TempDir
    lateinit var directory: File

    @Test
    fun `two mappers generating the same mapper should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.MapperCanBeDeclaredTwiceWithoutAnyCollisions.*

                class FirstMapper : ObjectMappie<FirstInput, FirstOutput>()
                class SecondMapper : ObjectMappie<SecondInput, SecondOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val firstMapper = classLoader
                .loadObjectMappieClass<FirstInput, FirstOutput>("FirstMapper")
                .constructors
                .first()
                .call()

            assertThat(firstMapper.map(FirstInput(NestedInput.A)))
                .isEqualTo(FirstOutput(NestedOutput.A))

            val secondMapper = classLoader
                .loadObjectMappieClass<SecondInput, SecondOutput>("SecondMapper")
                .constructors
                .first()
                .call()

            assertThat(secondMapper.map(SecondInput(NestedInput.B)))
                .isEqualTo(SecondOutput(NestedOutput.B))
        }
    }
}