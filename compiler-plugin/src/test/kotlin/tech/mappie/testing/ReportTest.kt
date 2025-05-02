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

                class Mapper private constructor(internal var a: String, private val b: Int = 1) : ObjectMappie<Input, Output>() {
                    val field: Int = 10
                    
                    enum class MyEnum { A, B; }

                    private val getter: String
                        get() { return "string" }
                    
                    internal var setter: Int
                        get() = 5
                        set(value) { println(value) }

                    val lazyVal by lazy { "1" }

                    private constructor(b: Int) : this("10", b) {
                        println("test")
                    }

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

                        var size: Long = 9223372036854775807L

                        private fun doWhile() {
                            var i = 0
                            do {
                                println(i)
                            } while (i++ >= size)
                        }

                        fun doTryCatch() {
                            try { throw Exception("test") } catch (e: Exception) { } finally { }
                        }
                    }
                }
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            hasOutputLines(
                """

                """.trimMargin()
            )
        }
    }
}