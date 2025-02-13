package tech.mappie.testing.compatibility.kotlin

import org.junit.jupiter.api.BeforeEach
import tech.mappie.testing.TestBase

abstract class KotlinCompatibilityTestBase : TestBase() {

    @BeforeEach
    fun files() {
        generateObjectMapper()
        generateEnumMapper()
        generateGeneratedEnumMapper()
        generateGeneratedObjectMapper()
    }

    private fun generateObjectMapper() {
        kotlin(
            "src/main/kotlin/ObjectMapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class InputObject(
                val name: String,
                val age: Int,
                val nested: NestedInput,
            )

            data class NestedInput(val boolean: BooleanEnum) {
                enum class BooleanEnum { TRUE, FALSE }
            }

            data class OutputObject(
                val name: String,
                val age: Int,
                val boolean: Boolean,
                val unknown: String,
            )

            object ObjectMapperWithoutVia : ObjectMappie<InputObject, OutputObject>() {
                override fun map(from: InputObject) = mapping {
                    to::boolean fromProperty from.nested::boolean
                    to("unknown") fromValue "unknown"
                }
            }
        
            @tech.mappie.api.config.UseStrictEnums
            object ObjectMapper : ObjectMappie<InputObject, OutputObject>() {
                override fun map(from: InputObject) = mapping {
                    to::boolean fromProperty from.nested::boolean via BooleanEnumToBooleanMapper
                    to("unknown") fromValue "unknown"
                }
            }

            @tech.mappie.api.config.UseDefaultArguments
            object BooleanEnumToBooleanMapper : ObjectMappie<NestedInput.BooleanEnum, Boolean>() {
                override fun map(from: NestedInput.BooleanEnum) = when (from) {
                    NestedInput.BooleanEnum.TRUE -> !false
                    NestedInput.BooleanEnum.FALSE -> !true
                }
            }
            """.trimIndent()
        )

        kotlin(
            "src/test/kotlin/ObjectMapperTest.kt",
            """
            import kotlin.test.*
            
            class ObjectMapperTest {
            
                @Test
                fun `map using ObjectMapper`() {
                    assertEquals(
                        OutputObject("name", 22, true, "unknown"),
                        ObjectMapper.map(InputObject("name", 22, NestedInput(NestedInput.BooleanEnum.TRUE)))
                    )
                }
            }
            """.trimIndent()
        )
    }

    private fun generateEnumMapper() {
        kotlin(
            "src/main/kotlin/EnumMapper.kt",
            """
                import tech.mappie.api.EnumMappie
    
                enum class InputEnum { A, B, C, D; }
    
                enum class OutputEnum { A, B, C, D, E; }
    
                object EnumMapper : EnumMappie<InputEnum, OutputEnum>()
                """.trimIndent()
        )

        kotlin(
            "src/test/kotlin/EnumMapperTest.kt",
            """
                import kotlin.test.Test
                import kotlin.test.assertEquals
    
                class EnumMapperTest {
    
                    private val mappings = listOf(
                        InputEnum.A to OutputEnum.A,
                        InputEnum.B to OutputEnum.B,
                        InputEnum.C to OutputEnum.C,
                        InputEnum.D to OutputEnum.D,
                    )
    
                    @Test
                    fun `map InputEnum to OutputEnum`() {
                        mappings.forEach { (input, expected) ->
                            assertEquals(
                                EnumMapper.map(input), expected
                            )
                        }
                    }
                }
                """.trimIndent()
        )
    }

    private fun generateGeneratedEnumMapper() {
        kotlin(
            "src/main/kotlin/GeneratedEnumMapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class NestedGeneratedInputObject(val enum: InputEnumTwo)

            enum class InputEnumTwo {
                A, B, C, D;
            }

            data class NestedGeneratedOutputObject(val enum: OutputEnumTwo)

            enum class OutputEnumTwo {
                A, B, C, D, E;
            }

            object NestedGeneratedInputToOutputMapper : ObjectMappie<NestedGeneratedInputObject, NestedGeneratedOutputObject>()
            """.trimIndent()
        )

        kotlin(
            "src/test/kotlin/GeneratedEnumMapperTest.kt",
            """
            import kotlin.test.*
            
            class GeneratedEnumMapperTest {
            
                private val mappings = listOf(
                    InputEnumTwo.A to OutputEnumTwo.A,
                    InputEnumTwo.B to OutputEnumTwo.B,
                    InputEnumTwo.C to OutputEnumTwo.C,
                    InputEnumTwo.D to OutputEnumTwo.D,
                )
            
                @Test
                fun `map NestedGeneratedInputObject to NestedGeneratedOutputObject`() {
                    mappings.forEach { (input, expected) ->
                        assertEquals(
                            NestedGeneratedInputToOutputMapper.map(NestedGeneratedInputObject(input)),
                            NestedGeneratedOutputObject(expected)
                        )
                    }
                }
            }
            """.trimIndent()
        )
    }

    private fun generateGeneratedObjectMapper() {
        kotlin(
            "src/main/kotlin/GeneratedObjectMapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class NestedGeneratedInputClassObject(val value: InputClass)

            data class InputClass(val value: Int)

            data class NestedGeneratedOutputClassObject(val value: OutputClass)

            data class OutputClass(val value: Int)

            object NestedGeneratedInputClassToOutputClassMapper : ObjectMappie<NestedGeneratedInputClassObject, NestedGeneratedOutputClassObject>()
            """.trimIndent()
        )

        kotlin(
            "src/test/kotlin/GeneratedObjectMapperTest.kt",
            """
            import kotlin.test.*
            
            class NestedGeneratedClassMapperTest {
            
                @Test
                fun `map NestedGeneratedInputClassObject to NestedGeneratedOutputClassObject`() {
                    assertEquals(
                        NestedGeneratedInputClassToOutputClassMapper.map(NestedGeneratedInputClassObject(InputClass(10))),
                        NestedGeneratedOutputClassObject(OutputClass(10))
                    )
                }
            }
            """.trimIndent()
        )
    }
}