package tech.mappie.testing.updating

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectUpdateMappieClass
import java.io.File

class CopyWithSameValueTest {

    data class Input(var first: String, val second: Int)
    data class Updater(val first: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `copy should take property`() {
        compile(directory, verbose = true) {
            file("Test.kt",
                """
                import tech.mappie.api.updating.ObjectUpdateMappie
                import tech.mappie.testing.updating.CopyWithSameValueTest.*

                class Mapper : ObjectUpdateMappie<Updater, Input>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectUpdateMappieClass<Updater, Input>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.update(Input("original", 1), Updater("updated")))
                .isEqualTo(Input("updated", 1))
        }
    }
}