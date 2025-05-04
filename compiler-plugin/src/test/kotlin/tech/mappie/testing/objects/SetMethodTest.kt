package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class SetMethodTest {
    data class Input(val age: Int)
    class Output {
        private var _age: Int = 0

        fun setAge(age: Int) {
            this._age = age
        }
    }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object via setter explicitly should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.SetMethodTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to("age") fromProperty from::age
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(25)))
                .usingRecursiveComparison()
                .isEqualTo(Output().apply { setAge(25) })
        }
    }

    @Test
    fun `map object via setter implicitly should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.SetMethodTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(50)))
                .usingRecursiveComparison()
                .isEqualTo(Output().apply { setAge(50) })
        }
    }
}