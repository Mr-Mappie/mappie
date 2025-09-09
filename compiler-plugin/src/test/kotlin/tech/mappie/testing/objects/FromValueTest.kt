package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class FromValueTest : MappieTestCase() {

    data class Output(val value: String?)

    @Test
    fun `map property fromValue should succeed`() {
        compile {
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

            val mapper = objectMappie<Unit, Output>()

            assertThat(mapper.map(Unit)).isEqualTo(Output(Unit::class.simpleName!!))
        }
    }

    @Test
    fun `map property fromValue null should succeed`() {
        compile {
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

            val mapper = objectMappie<Unit, Output>()

            assertThat(mapper.map(Unit))
                .isEqualTo(Output(null))
        }
    }

    @Test
    fun `map property fromValue using extension receiver on mapping dsl should fail`() {
        compile {
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
                "The function run was called as an extension method on the mapping dsl which does not exist after compilation. Did you mean to use kotlin.run?",
            )
        }
    }

    @Test
    fun `map property fromValue using dispatch receiver on mapping dsl should fail`() {
        compile {
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
            hasErrorMessage(6, "The function toString was called on the mapping dsl which does not exist after compilation")
        }
    }
}