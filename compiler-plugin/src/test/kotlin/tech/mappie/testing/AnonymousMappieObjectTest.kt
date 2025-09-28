package tech.mappie.testing

import org.junit.jupiter.api.Test

class AnonymousMappieObjectTest : MappieTestCase() {

    data class Input(val text: String)
    data class Output(val text: String)

    @Test
    fun `anonymous mappie object should fail`() {
        compile {
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