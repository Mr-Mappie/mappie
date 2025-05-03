package tech.mappie.testing.configuration

import tech.mappie.testing.MavenTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class MavenReportTest : MavenTestBase() {

    override val mappieOptions = mapOf(
        "report-enabled" to "true"
    )

    @Test
    fun `report enabled`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            data class Input(val first: String)
            data class Output(val first: String, val second: Int = 1)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        assertThat(execute()).isSuccessful()
        assertThat(File(directory, "target/mappie/Mapper.kt")).exists()
    }
}