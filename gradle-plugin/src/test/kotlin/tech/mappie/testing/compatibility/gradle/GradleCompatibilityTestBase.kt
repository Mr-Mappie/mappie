package tech.mappie.testing.compatibility.gradle

import org.junit.jupiter.api.BeforeEach
import tech.mappie.testing.TestBase

abstract class GradleCompatibilityTestBase : TestBase() {

    @BeforeEach
    fun files() {
        kotlin(
            "src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class Input(val value: String)
            data class Output(val value: String)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        kotlin(
            "src/test/kotlin/MapperTest.kt",
            """
            import kotlin.test.*

            class MapperTest {
            
                @Test
                fun map() {
                    assertEquals(Output("test"), Mapper.map(Input("test")))
                }
            }
            """.trimIndent()
        )
    }
}