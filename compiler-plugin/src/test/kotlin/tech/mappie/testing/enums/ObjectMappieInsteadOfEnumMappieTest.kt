package tech.mappie.testing.enums

import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File
import kotlin.test.Test

class ObjectMappieInsteadOfEnumMappieTest {

    data class InputObject(val boolean: Boolean)
    enum class Input { TRUE, FALSE }

    enum class Output { TRUE, FALSE }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map enum to enum in ObjectMappie should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.ObjectMappieInsteadOfEnumMappieTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies  {
            isCompilationError()
            hasErrorMessage(4, "Target type Output cannot be an enum class", listOf("Override EnumMappie instead of ObjectMappie"))
        }
    }

    @Test
    fun `map object to enum explicitly in ObjectMappie should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.ObjectMappieInsteadOfEnumMappieTest.*

                class Mapper : ObjectMappie<InputObject, Output>() {
                    override fun map(from: InputObject) = 
                    if (from.boolean) Output.TRUE else Output.FALSE
                }
                """
            )
        } satisfies  {
            isOk()
        }
    }

    @Test
    fun `map object to enum implicitly in ObjectMappie should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.ObjectMappieInsteadOfEnumMappieTest.*

                class Mapper : ObjectMappie<InputObject, Output>()
                """
            )
        } satisfies  {
            isCompilationError()
            hasErrorMessage(4, "Target type Output cannot be an enum class", listOf("Override EnumMappie instead of ObjectMappie"))
        }
    }
}
