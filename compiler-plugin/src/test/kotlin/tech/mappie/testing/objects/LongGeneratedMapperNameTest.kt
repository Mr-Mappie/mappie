package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class LongGeneratedMapperNameTest : MappieTestCase() {
    data class Input(val nested: SourceDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainRecord)
    data class Output(val nested: TargetDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainRecord)

    data class SourceDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainRecord(val value: String)
    data class TargetDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainRecord(val value: String)

    @Test
    fun `implicitly map properties whose type names form an oversized class file name`() =
        assertLongTypeNamesMap("Mapper")

    @Test
    fun `implicitly map properties when the enclosing mapper leaves no room for a nested class file`() =
        assertLongTypeNamesMap("Mapper".padEnd(210, 'X'), verifyReport = true)

    private fun assertLongTypeNamesMap(mapperName: String, verifyReport: Boolean = false) {
        compile(verbose = verifyReport) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.LongGeneratedMapperNameTest.*

                class $mapperName : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            if (!verifyReport) hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>(mapperName)
            val input = Input(SourceDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainRecord("value"))
            val expected = Output(TargetDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainDomainRecord("value"))
            assertThat(mapper.map(input)).isEqualTo(expected)
            if (verifyReport) {
                val helper = directory.resolve("classes").listFiles().orEmpty()
                    .single { it.extension == "class" && it.nameWithoutExtension != mapperName }
                val helperName = helper.nameWithoutExtension
                val sourceName = input.nested.javaClass.simpleName
                val targetName = expected.nested.javaClass.simpleName
                assertHasLogMessage("public object $helperName: ObjectMappie<$sourceName, $targetName>() {")
            }
        }
    }

    @Test
    fun `implicitly map deeply nested properties`() {
        val depth = 10
        val models = (0 until depth).joinToString("\n") { level ->
            """
            data class SourceLevel$level(val nested: SourceLevel${level + 1})
            data class TargetLevel$level(val nested: TargetLevel${level + 1})
            """.trimIndent()
        }
        val input = (depth downTo 0).fold("\"value\"") { value, level -> "SourceLevel$level($value)" }
        val result = ".nested".repeat(depth) + ".value"

        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie

                $models
                data class SourceLevel$depth(val value: String)
                data class TargetLevel$depth(val value: String)

                class DeepMapper : ObjectMappie<SourceLevel0, TargetLevel0>()

                fun mapValue(): String = DeepMapper().map($input)$result
                """.trimIndent()
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(classLoader.loadClass("TestKt").getMethod("mapValue").invoke(null))
                .isEqualTo("value")
        }
    }
}
