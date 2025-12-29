package tech.mappie.testing.modules

import tech.mappie.testing.MavenTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieModules

class MavenKotlinxCollectionsImmutableTest : MavenTestBase() {

    override val modules = setOf(MappieModules.MODULE_KOTLINX_COLLECTIONS_IMMUTABLE)

    @Test
    fun `module kotlinx-collections-immutable can be used`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            import kotlinx.collections.immutable.*
            
            data class Input(val first: List<String>)
            data class Output(val first: ImmutableList<String>)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        assertThat(execute()).isSuccessful()
    }
}