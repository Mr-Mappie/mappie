package tech.mappie.testing

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile

import java.io.File

class AnonymousMappieObjectTest {

    data class Input(val text: String)
    data class Output(val text: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `anonymous mappie object should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.AnonymousMappieObjectTest.*

                fun test() {
                    val x = object : ObjectMappie<Int, String>() { }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(5, "Anonymous Mappie objects are not supported")
        }
    }
}