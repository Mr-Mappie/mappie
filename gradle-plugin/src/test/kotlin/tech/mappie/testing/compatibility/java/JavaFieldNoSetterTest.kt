package tech.mappie.testing.compatibility.java

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.TestBase

class JavaFieldNoSetterTest : TestBase() {

    @Test
    fun `map Kotlin class to Java class without setter should fail`() {
        java("src/main/java/JavaClass.java",
            """
            import java.util.Objects;
            
            public class JavaClass {
                private String value;
                                   
                public String getValue() {
                    return value;
                }
                                        
                @Override 
                public boolean equals(Object obj) {
                    if (obj == null) return false;
                    if (obj.getClass() != this.getClass()) return false;
                    return Objects.equals(((JavaClass) obj).value, this.value);
                }
            }
            """.trimIndent()
        )

        kotlin("src/main/kotlin/KotlinClass.kt",
            """
            data class KotlinClass(val value: String)
            """.trimIndent()
        )

        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            object Mapper : ObjectMappie<KotlinClass, JavaClass>() {
                override fun map(from: KotlinClass) = mapping {
                    to("value") fromProperty from::value
                }
            }
            """.trimIndent()
        )

        kotlin("src/test/kotlin/MapperTest.kt",
            """
            import kotlin.test.*
            
            class MapperTest {
            
                @Test
                fun test() {
                    assertEquals(
                        "value",
                        Mapper.map(KotlinClass("value")).value
                    )
                }
            }
            """.trimIndent()
        )

        val result = runner.withArguments("build").buildAndFail()

        assertThat(result.output.lines())
            .anyMatch { it.matches(Regex("e: .+ Identifier 'value' does not occur as a setter or as a parameter in constructor")) }
    }
}