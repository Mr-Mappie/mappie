package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ComplexNestedMapperTest {

    data class InputObject(
        val name: String,
        val age: Int,
        val nested: NestedInput,
    )

    @Suppress("unused")
    data class NestedInput(val boolean: BooleanEnum) {
        enum class BooleanEnum { TRUE, FALSE }
    }

    data class OutputObject(
        val name: String,
        val age: Int,
        val boolean: Boolean,
    )

    @TempDir
    lateinit var directory: File

    @Test
    fun `map complex nested object`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ComplexNestedMapperTest.*

                class Mapper : ObjectMappie<InputObject, OutputObject>() {
            
                    override fun map(from: InputObject) = mapping {
                        to::boolean fromProperty from.nested::boolean
                    }
                }
            
                private object BooleanEnumToBooleanMapper : ObjectMappie<NestedInput.BooleanEnum, Boolean>() {
                    override fun map(from: NestedInput.BooleanEnum): Boolean = when (from) {
                        NestedInput.BooleanEnum.TRUE -> true
                        NestedInput.BooleanEnum.FALSE -> false
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<InputObject, OutputObject>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(InputObject("test", 34, NestedInput(NestedInput.BooleanEnum.TRUE))))
                .isEqualTo(OutputObject("test", 34, true))
        }
    }

    @Test
    fun `map complex nested with explicit via object`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ComplexNestedMapperTest.*

                class Mapper : ObjectMappie<InputObject, OutputObject>() {
            
                    override fun map(from: InputObject) = mapping {
                        to::boolean fromProperty from.nested::boolean via BooleanEnumToBooleanMapper
                    }
                }
            
                private object BooleanEnumToBooleanMapper : ObjectMappie<NestedInput.BooleanEnum, Boolean>() {
                    override fun map(from: NestedInput.BooleanEnum): Boolean = when (from) {
                        NestedInput.BooleanEnum.TRUE -> true
                        NestedInput.BooleanEnum.FALSE -> false
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<InputObject, OutputObject>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(InputObject("test", 34, NestedInput(NestedInput.BooleanEnum.TRUE))))
                .isEqualTo(OutputObject("test", 34, true))
        }
    }
}