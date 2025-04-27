package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GetMethodTest {
    class Input {
        private var _age: Int = 0

        fun setAge(age: Int) {
            this._age = age
        }
        fun getAge() = _age
    }

    data class Output(val age: Int)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object via getter explicitly should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GetMethodTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::age fromValue from.getAge()
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

            assertThat(mapper.map(Input().apply { setAge(20) }))
                .usingRecursiveComparison()
                .isEqualTo(Output(20))
        }
    }

    @Test
    fun `map object via getter implicitly should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GetMethodTest.*

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

            assertThat(mapper.map(Input().apply { setAge(60) }))
                .usingRecursiveComparison()
                .isEqualTo(Output(60))
        }
    }
}