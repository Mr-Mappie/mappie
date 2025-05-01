package tech.mappie.testing

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File

class ReportTest {

    data class Input(val text: String, val inner: InnerInput)
    data class InnerInput(val int: Int)

    data class Output(val text: String, val inner: InnerOutput)
    data class InnerOutput(val int: Int)

    @TempDir
    lateinit var directory: File

    @Test
    fun `validate report`() {
        compile(directory, verbose = true) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.ReportTest.*

                class Mapper(internal var a: String, private val b: Int = 1) : ObjectMappie<Input, Output>() {
                    val field: Int = 10
                                    
                    private val getter: String
                        get() { return "string" }
                    
                    internal var setter: Int
                        get() = 5
                        set(value) { println(value) }         

                    init {
                        for (i in (0..<10)) {
                            if (i % 2 == 0) {
                                break 
                            } else {
                                continue
                            }
                        }
                    }

                    override fun mapList(inputs: List<Input>) = inputs.map { return emptyList<Output>(); map(it) }

                    companion object {
                        internal const val X = "1"
                    }
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