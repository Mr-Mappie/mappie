package tech.mappie.testing.modules

import tech.mappie.testing.TestBase
import org.junit.jupiter.api.Test
import tech.mappie.testing.KotlinPlatform
import tech.mappie.testing.MappieModules.MODULE_KOTLINX_COLLECTIONS_IMMUTABLE

class KotlinxCollectionsImmutableMultiplatformTest : TestBase() {

    override val platform = KotlinPlatform.MULTIPLATFORM

    override val modules = setOf(MODULE_KOTLINX_COLLECTIONS_IMMUTABLE)

    @Test
    fun `module kotlinx-datetime can be used in multiplatform`() {
        kotlin("src/jvmMain/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie
            import kotlinx.collections.immutable.*
            
            data class Input(val first: List<String>)
            data class Output(val first: ImmutableList<String>)
    
            object Mapper : ObjectMappie<Input, Output>()
            """.trimIndent()
        )

        kotlin("src/jvmTest/kotlin/MapperTest.kt",
            """
            import kotlin.test.*
            import kotlinx.collections.immutable.*

            class JvmMapperTest {

                @Test
                fun `map Input to Output`() {
                    assertEquals(
                        Output(immutableListOf("value")),
                        Mapper.map(Input(listOf("value"))),
                    )
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}