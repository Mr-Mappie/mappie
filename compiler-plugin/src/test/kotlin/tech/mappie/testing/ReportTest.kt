package tech.mappie.testing

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File

class ReportTest {

    data class Input(val text: String)
    data class Output(val text: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `validate report`() {
        compile(directory, verbose = true) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.ReportTest.*

                class Mapper(
                    internal var a: String, 
                    private val b: Int = 1,
                ) : ObjectMappie<Input, Output>() {
                    override fun mapList(inputs: List<Input>) = inputs.map { return emptyList<Output>(); map(it) }
                }
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            hasOutputLines(
                """
                |public class Mapper: ObjectMappie<Input, Output>()
                |{
                |    fun mapList(inputs: List<Input>): List<Output>
                |    {
                |         return inputs.map(
                |         {
                |             return this.map(it)
                |         })
                |    }
                |    
                |    fun map(from: Input): Output
                |    {
                |        val tmp: Output = Output(from.text)
                |        return tmp
                |    }
                |
                |}
                """.trimMargin()
            )
        }
    }
}