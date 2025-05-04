package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GenericInputPropertyTest {

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

    @TempDir
    lateinit var directory: File

    @Test
    fun `map property fromValue should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<FooDto, Wrapper<Foo>>("WrapperMapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(FooDto("id", "name", "description")))
                .isEqualTo(Wrapper(Foo("id", "name", "description"), "name"))
        }
    }
}