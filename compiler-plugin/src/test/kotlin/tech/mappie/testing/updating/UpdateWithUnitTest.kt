package tech.mappie.testing.updating

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectUpdateMappieClass
import java.io.File

class UpdateWithUnitTest {

    data class Input(var first: String, var second: Int)

    @TempDir
    lateinit var directory: File

    @Test
    fun `update using Unit should warn`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.updating.ObjectUpdateMappie
                import tech.mappie.testing.updating.UpdateWithUnitTest.*

                class Mapper : ObjectUpdateMappie<Unit, Input>()
                """
            )
        } satisfies {
            isOk()
            hasWarningMessage(4, "Class does not update anything")

            val mapper = classLoader
                .loadObjectUpdateMappieClass<Unit, Input>("Mapper")
                .constructors
                .first()
                .call()

            val source = Input("original", 1)
            assertThat(mapper.update(source, Unit))
                .isSameAs(source)
        }
    }

    @Test
    fun `update using Unit and fromValue should only use fromValue`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.updating.ObjectUpdateMappie
                import tech.mappie.testing.updating.UpdateWithUnitTest.*

                class Mapper : ObjectUpdateMappie<Unit, Input>() {
                    override fun update(source: Input, updater: Unit) = updating {
                        to::second fromValue 10
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectUpdateMappieClass<Unit, Input>("Mapper")
                .constructors
                .first()
                .call()

            val source = Input("original", 1)
            assertThat(mapper.update(source, Unit))
                .isSameAs(source.apply { second = 10 })
        }
    }
}