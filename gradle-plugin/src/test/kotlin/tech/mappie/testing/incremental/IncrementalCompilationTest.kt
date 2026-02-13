package tech.mappie.testing.incremental

import org.junit.jupiter.api.Test
import tech.mappie.testing.TestBase

class IncrementalCompilationTest : TestBase() {

    @Test
    fun `incremental compilation with a single mapper updated`() {
        kotlin(
            "src/main/kotlin/Range.kt",
            """
            import java.time.LocalDate
            
            class Range(
                val startsOn: LocalDate,
                val endsOn: LocalDate,
            )
            """.trimIndent()
        )

        kotlin(
            "src/main/kotlin/RangeResource.kt",
            """
            class RangeResource(
                val startsOn: Long,
                val endsOn: Long,
            )
            """.trimIndent()
        )

        kotlin("src/main/kotlin/TimeMappers.kt",
            """
            import tech.mappie.api.ObjectMappie
            import java.time.LocalDate
            
            object LocalToLocalDateTimeMapper : ObjectMappie<LocalDate, Long>() {
                override fun map(from: LocalDate): Long = from.toEpochDay()
            }
            
            object LocalDateTimeToLocalDateMapper : ObjectMappie<Long, LocalDate>() {
                override fun map(from: Long): LocalDate = LocalDate.ofEpochDay(from)
            }
            """.trimIndent())

        kotlin(
            "src/main/kotlin/RangeMappers.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            object RangeToResource : ObjectMappie<Range, RangeResource>()
            object ResourceToRange : ObjectMappie<RangeResource, Range>()
            """.trimIndent()
        )

        runner.withArguments("build").build()

        delete("src/main/kotlin/RangeMappers.kt")

        kotlin(
            "src/main/kotlin/RangeMappers.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            object RangeToResource : ObjectMappie<Range, RangeResource>()
            object ResourceToRange : ObjectMappie<RangeResource, Range>() {
                private val x = 1
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
    
    @Test
    fun `incremental compilation with all mappers updated`() {
        kotlin(
            "src/main/kotlin/Range.kt",
            """
            import java.time.LocalDate
            
            class Range(
                val startsOn: LocalDate,
                val endsOn: LocalDate,
            )
            """.trimIndent()
        )

        kotlin(
            "src/main/kotlin/RangeResource.kt",
            """
            class RangeResource(
                val startsOn: Long,
                val endsOn: Long,
            )
            """.trimIndent()
        )

        kotlin("src/main/kotlin/TimeMappers.kt",
            """
            import tech.mappie.api.ObjectMappie
            import java.time.LocalDate
            
            object LocalToLocalDateTimeMapper : ObjectMappie<LocalDate, Long>() {
                override fun map(from: LocalDate): Long = from.toEpochDay()
            }
            
            object LocalDateTimeToLocalDateMapper : ObjectMappie<Long, LocalDate>() {
                override fun map(from: Long): LocalDate = LocalDate.ofEpochDay(from)
            }
            """.trimIndent())

        kotlin(
            "src/main/kotlin/RangeMappers.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            object RangeToResource : ObjectMappie<Range, RangeResource>()
            object ResourceToRange : ObjectMappie<RangeResource, Range>()
            """.trimIndent()
        )

        runner.withArguments("build").build()

        delete("src/main/kotlin/TimeMappers.kt")
        delete("src/main/kotlin/RangeMappers.kt")

        kotlin("src/main/kotlin/TimeMappers.kt",
            """
            import tech.mappie.api.ObjectMappie
            import java.time.LocalDate
            
            object LocalToLocalDateTimeMapper : ObjectMappie<LocalDate, Long>() {
                private val x = 1

                override fun map(from: LocalDate): Long = from.toEpochDay()
            }
            
            object LocalDateTimeToLocalDateMapper : ObjectMappie<Long, LocalDate>() {
                override fun map(from: Long): LocalDate = LocalDate.ofEpochDay(from)
            }
            """.trimIndent())

        kotlin(
            "src/main/kotlin/RangeMappers.kt",
            """
            import tech.mappie.api.ObjectMappie
            
            object RangeToResource : ObjectMappie<Range, RangeResource>()
            object ResourceToRange : ObjectMappie<RangeResource, Range>() {
                private val x = 1
            }
            """.trimIndent()
        )

        runner.withArguments("build").build()
    }
}