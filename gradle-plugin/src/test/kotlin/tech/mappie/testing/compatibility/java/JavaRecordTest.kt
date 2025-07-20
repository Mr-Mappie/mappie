package tech.mappie.testing.compatibility.java

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.mappie.testing.TestBase

class JavaRecordTest : TestBase() {

    @BeforeEach
    fun setUp() {
        java("src/main/java/Input.java",
            """
            public record Input(double number) { }
            """.trimIndent()
        )

        java("src/main/java/Output.java",
            """
            public record Output(double number) { }
            """.trimIndent()
        )
    }

    @Test
    fun `map Java record to Java record`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        kotlin("src/test/kotlin/MapperTest.kt",
            """
            import kotlin.test.*
            
            class MapperTest {
            
                @Test
                fun test() {
                    assertEquals(Output(10.0), Mapper.map(Input(10.0)))
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}