package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GenericTargetPropertyTest : MappieTestCase() {

    data class FooDto(
        val id: String,
        val name: String,
        val description: String
    )

    data class Foo(
        val uniqueId: String,
        val name: String,
        val description: String
    )

    data class Wrapper<W>(
        val wrapped: W,
        val name: String
    )

    @Test
    fun `map property fromValue should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericTargetPropertyTest.*

                class FooMapper : ObjectMappie<FooDto, Foo>() {
                    override fun map(from: FooDto): Foo = mapping {
                        to::uniqueId fromProperty from::id
                    }
                }
            
                class WrapperMapper : ObjectMappie<FooDto, Wrapper<Foo>>() {
                    override fun map(from: FooDto): Wrapper<Foo> = mapping {
                        to::wrapped fromValue FooMapper().map(from)
                        to::name fromProperty from::name
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<FooDto, Wrapper<Foo>>("WrapperMapper")

            assertThat(mapper.map(FooDto("id", "name", "description")))
                .isEqualTo(Wrapper(Foo("id", "name", "description"), "name"))
        }
    }
}