package tech.mappie.testing.objects3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile

import tech.mappie.testing.loadObjectMappie3Class
import java.io.File

class Object3WithOverlappingValuesTest {

    data class Input1(val value: String, val char: Char)
    data class Input2(val value: String, val age: Int)
    data class Input3(val age: Int)
    data class Output(val value: String, val age: Int, val char: Char)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map identical data classes should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie3
                import tech.mappie.testing.objects3.Object3WithOverlappingValuesTest.*

                class Mapper : ObjectMappie3<Input1, Input2, Input3, Output>()
                """
            )
        } satisfies  {
            isCompilationError()
            hasErrorMessage(4, "Target Output::age has multiple sources defined")
        }
    }

    @Test
    fun `map identical data classes should with one specified succeed`() {
        compile(directory) {
                    file("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie3
                        import tech.mappie.testing.objects3.Object3WithOverlappingValuesTest.*
    
                        class Mapper : ObjectMappie3<Input1, Input2, Input3, Output>() {
                            override fun map(first: Input1, second: Input2, third: Input3) = mapping {
                                to::value fromProperty first::value
                                to::age fromProperty Input3::age
                            }
                        }
                        """
                    )        
           } satisfies {
           isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappie3Class<Input1, Input2, Input3, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input1("value", 'q'), Input2("second", 10), Input3(20)))
                .isEqualTo(Output("value", 20, 'q')
            )
        }
    }
}