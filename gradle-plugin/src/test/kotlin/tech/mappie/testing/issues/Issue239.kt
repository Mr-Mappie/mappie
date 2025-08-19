package tech.mappie.testing.issues

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.mappie.testing.TestBase

class Issue239 : TestBase() {

    @BeforeEach
    fun setUp() {
        kotlin("src/main/java/Input.kt",
            """
            import java.util.UUID

            data class Input(val id: UUID, val name: String)
            """.trimIndent()
        )

        java("src/main/java/Output.java",
            """
            import java.util.UUID;
            
            public class Output {
                private UUID id;
                private String name;
                        
                public UUID getId() {
                    return id;
                }
            
                public void setId(UUID id) {
                    this.id = id;
                }
            
                public String getName() {
                    return name;
                }
            
                public void setName(String name) {
                    this.name = name;
                }
                    
                @Override
                public boolean equals(Object o) {
                    if (o instanceof Output) {
                        return this.id.equals(((Output) o).id)
                            && this.name.equals(((Output) o).name);  
                    } else {
                        return false;
                    }
                } 
            }
            """.trimIndent()
        )
    }

    @Test
    fun `test issue 239`() {
        kotlin("src/main/kotlin/Mapper.kt",
            """
            import tech.mappie.api.ObjectMappie

            object Mapper: ObjectMappie<Input, Output>() {
                override fun map(from: Input): Output = mapping {
                    to("id") fromProperty from::id
                    to("name") fromProperty from::name
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
                    val uuid = java.util.UUID.randomUUID()
                    val name = "test"

                    val output = Output().apply {
                        setId(uuid)
                        setName(name)           
                    }

                    assertEquals(output, Mapper.map(Input(uuid, name)))
                }
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}