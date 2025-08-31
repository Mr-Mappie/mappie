package tech.mappie.testing

import org.junit.jupiter.api.Test

class ReportTest : MappieTestCase() {

    data class Input(val text: String, val inner: InnerInput)
    data class InnerInput(val int: Int)

    data class Output(val text: String, val inner: InnerOutput)
    data class InnerOutput(val int: Int)

    @Test
    fun `validate report`() {
        compile(verbose = true) {
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
        } satisfies {
            isOk()

            hasOutputLines(
                """
                |public class Mapper public constructor(internal var a: String, private val b: Int = 1): ObjectMappie<Input, Output>() {
                |    public val field: Int = 10
                |    public enum class MyEnum public constructor(): Enum<MyEnum>() {
                |        A,
                |        B,
                |    }
                |    private val getter
                |        get {
                |            return "string"
                |        }
                |    internal var setter
                |        get {
                |            return 5
                |        }
                |        set {
                |            println(value)
                |        }
                |    public val lazyVal: Lazy<String> = lazy({"1"})
                |        get {
                |            return lazyVal${'$'}delegate.getValue(this, Mapper::lazyVal)
                |        }
                |    private constructor(b: Int) {
                |        Mapper("10", b)
                |        println("test")
                |    }
                |    init {
                |        {
                |            val iterator: IntIterator = (0 ..< 10).iterator()
                |            while (iterator.hasNext()) {
                |                val i: Int = iterator.next()
                |                {
                |                    when {
                |                        ((i % 2) == 0) -> {
                |                            break
                |                        }
                |                        true -> {
                |                            continue
                |                        }
                |                    }
                |                }
                |            }
                |        }
                |    }
                |    override fun mapList(inputs: List<Input>): List<Output> {
                |        return inputs.map({it: Input -> return emptyList(); this.map(it)})
                |    }
                |    public object Companion: Any() {
                |        internal const val X: String = "1"
                |        public var size: Long = 9223372036854775807L
                |        fun doWhile(): Unit {
                |            var i: Int = 0
                |            {
                |                do {
                |                    println(i)
                |                } while (({
                |                    val unary: Int = i
                |                    i = unary.inc()
                |                    unary
                |                }.toLong() >= this.size))
                |            }
                |        }
                |        fun doTryCatch(): Unit {
                |            try {
                |                throw Exception(p0 = "test")
                |            }
                |            catch (val e: Exception) {
                |            }
                |
                |            finally {
                |            }
                |        }
                |    }
                |    override fun map(from: Input): Output {
                |        val tmp: Output = Output(text = from.text, inner = InnerInputToInnerOutputMapper.map(from.inner))
                |        return tmp
                |    }
                |    public object InnerInputToInnerOutputMapper: ObjectMappie<InnerInput, InnerOutput>() {
                |        override fun map(from: InnerInput): TO {
                |            val tmp: InnerOutput = InnerOutput(int = from.int)
                |            return tmp
                |        }
                |    }
                |}
                """.trimMargin()
            )
        }
    }
}