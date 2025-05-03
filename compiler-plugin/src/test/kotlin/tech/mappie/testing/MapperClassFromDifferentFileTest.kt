package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File

class MapperClassFromDifferentFileTest {

    data class Input(val inner: InnerInput)
    data class InnerInput(val string: String)
    
    data class Output(val inner: InnerOutput)
    data class InnerOutput(val string: String)
    
    @TempDir
    lateinit var directory: File
    
    @Test
    fun `mapper class from different file should succeed`() {
        compile(directory) {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.MapperClassFromDifferentFileTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )

            file("InnerMapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.MapperClassFromDifferentFileTest.*

                class InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(InnerInput("test"))))
                .isEqualTo(Output(InnerOutput("test")))
        }
    }
}