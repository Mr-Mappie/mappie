package tech.mappie.testing.configuration.compatibility.kotlin

import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat
import tech.mappie.BuildConfig
import tech.mappie.testing.MavenTestBase

class MavenOlderKotlinVersionCompatibilityTest : MavenTestBase() {

    override val kotlinVersion: String = "2.2.0"

    @Test
    fun `the maven plugin warns if an older kotlin version is used`() {
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

        val exptectedVersion = BuildConfig.VERSION.split('-').first()

        assertThat(execute())
        assertThat(logs.lines())
            .anyMatch { it.matches(Regex("\\[WARNING\\] Mappie unsupported Kotlin version $kotlinVersion, $exptectedVersion was expected. This is highly likely to lead to compilation failure.")) }
    }
}