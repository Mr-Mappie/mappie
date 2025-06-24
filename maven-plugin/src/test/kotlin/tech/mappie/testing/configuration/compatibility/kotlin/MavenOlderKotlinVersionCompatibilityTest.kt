package tech.mappie.testing.configuration.compatibility.kotlin

import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat
import tech.mappie.testing.MavenTestBase

class MavenOlderKotlinVersionCompatibilityTest : MavenTestBase() {

    override val kotlinVersion: String = "2.1.10"

    @Test
    fun `the maven plugin applies the compiler plugin`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            data class Input(val value: String)
            data class Output(val value: String)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        kotlin("src/test/kotlin/MapperTest.kt",
            """
            import org.testng.Assert.assertEquals
            import org.testng.annotations.*

            class MapperTest {
            
                @Test
                fun map() {
                    assertEquals(Output("test"), Mapper.map(Input("test")))
                }
            }
            """.trimIndent()
        )

        assertThat(execute()).isFailure()
        assertThat(logs.lines())
            .anyMatch { it.matches(Regex("\\[WARNING\\] Mappie unsupported Kotlin version 2.1.10, 2.2.0 was expected. This is highly likely to lead to compilation failure.")) }
    }
}