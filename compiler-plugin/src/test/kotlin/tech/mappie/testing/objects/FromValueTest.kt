package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class FromValueTest {

    data class Output(val value: String?)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map property fromValue should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.FromValueTest.*

                class Mapper : ObjectMappie<Unit, Output>() {
                    override fun map(from: Unit) = mapping {
                        Output::value fromValue Unit::class.simpleName!!
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Unit, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Unit)).isEqualTo(Output(Unit::class.simpleName!!))
        }
    }

    @Test
    fun `map property fromValue null should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.FromValueTest.*

                class Mapper : ObjectMappie<Unit, Output>() {
                    override fun map(from: Unit) = mapping {
                        Output::value fromValue null
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Unit, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Unit)).isEqualTo(Output(null))
        }
    }

    @Test
    fun `map property fromValue using extension receiver on mapping dsl should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.FromValueTest.*

                class Mapper : ObjectMappie<Unit, Output>() {
                    override fun map(from: Unit) = mapping {
                        Output::value fromValue run {
                            "test"
                        }
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6,
                "The function 'run' was called as an extension method on the mapping dsl which does not exist after compilation",
            )
        }
    }

    @Test
    fun `map property fromValue using extension receiver on mapping dsl in ObjectMappie2 should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie2
                import tech.mappie.testing.objects.FromValueTest.*

                class Mapper : ObjectMappie2<Unit, Unit, Output>() {
                    override fun map(first: Unit, second: Unit) = mapping {
                        to::value fromValue run {
                            "test"
                        }
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6,
                "The function 'run' was called as an extension method on the mapping dsl which does not exist after compilation",
            )
        }
    }

    @Test
    fun `map property fromValue using dispatch receiver on mapping dsl should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.FromValueTest.*

                class Mapper : ObjectMappie<Unit, Output>() {
                    override fun map(from: Unit) = mapping {
                        Output::value fromValue toString()
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6, "The function 'toString' was called on the mapping dsl which does not exist after compilation")
        }
    }
}