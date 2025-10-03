package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class NestedNullToNonNullPropertyTest : MappieTestCase() {

    data class Input(val text: InnerInput?, val int: Int)
    data class InnerInput(val value: String)
    data class Output(val text: InnerOutput, val int: Int)
    data class InnerOutput(val value: String)

    @Test
    fun `map object with nested null to non-null implicit should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNonNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage( 4, "Target Output::text automatically resolved from Input::text via InnerMapper but cannot assign source type InnerOutput? to target type InnerOutput")
        }
    }

    @Test
    fun `map object with nested null to non-null explicit without via should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNonNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() { 
                    override fun map(from: Input) = mapping {
                        to::text fromProperty from::text
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(
                6,
                "Target Output::text of type InnerOutput cannot be assigned from from::text via InnerMapper of type InnerOutput?"
            )
        }
    }

    @Test
    fun `map object with nested null to non-null explicit fromPropertyNotNull without via should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNonNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() { 
                    override fun map(from: Input) = mapping {
                        to::text fromPropertyNotNull from::text
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("test"), 1)))
                .isEqualTo(Output(InnerOutput("test"), 1))
        }
    }

    @Test
    fun `map object with nested null to non-null explicit should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNonNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() { 
                    override fun map(from: Input) = mapping {
                        to::text fromProperty from::text via InnerMapper
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(
                6,
                "Target Output::text of type InnerOutput cannot be assigned from from::text via InnerMapper of type InnerOutput?"
            )
        }
    }

    @Test
    fun `map object with nested null to non-null fromPropertyNotNull explicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNonNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() { 
                    override fun map(from: Input) = mapping {
                        to::text fromPropertyNotNull from::text via InnerMapper
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("test"), 1)))
                .isEqualTo(Output(InnerOutput("test"), 1))
        }
    }

    @Test
    @Disabled("Explicit nullable mapping source in Mappie is not supported yet.")
    fun `map object with nested null to non-null with implicit null mapper should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNonNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() { 
                    override fun map(from: Input) = mapping {
                        to::text fromProperty from::text
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput?, InnerOutput>() {
                    override fun map(from: InnerInput?) = if (from == null) error("error") else mapping()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("test"), 1)))
                .isEqualTo(Output(InnerOutput("test"), 1))

            assertThatThrownBy { mapper.map(Input(null, 1)) }
                .isInstanceOf(IllegalStateException::class.java)
        }
    }

    @Test
    @Disabled("Explicit nullable mapping source in Mappie is not supported yet.")
    fun `map object with nested null to non-null with explicit null mapper should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNonNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() { 
                    override fun map(from: Input) = mapping {
                        to::text fromProperty from::text via InnerMapper
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput?, InnerOutput>() {
                    override fun map(from: InnerInput?) = if (from == null) error("error") else mapping()
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("test"), 1)))
                .isEqualTo(Output(InnerOutput("test"), 1))

            assertThatThrownBy { mapper.map(Input(null, 1)) }
                .isInstanceOf(IllegalStateException::class.java)
        }
    }
}