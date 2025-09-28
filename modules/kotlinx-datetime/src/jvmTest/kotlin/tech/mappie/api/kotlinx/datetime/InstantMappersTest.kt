package tech.mappie.api.kotlinx.datetime

import java.time.Instant as JInstant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@OptIn(ExperimentalTime::class)
class InstantMappersTest : MappieTestCase() {

    data class InstantWrapper(val value: Instant)

    data class JInstantWrapper(val value: JInstant)

    @Test
    fun `map Kotlin Instant to Java Instant implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.InstantMappersTest.*

                class Mapper : ObjectMappie<InstantWrapper, JInstantWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = Instant.DISTANT_FUTURE

            val mapper = objectMappie<InstantWrapper, JInstantWrapper>()

            assertThat(mapper.map(InstantWrapper(input)))
                .isEqualTo(JInstantWrapper(input.toJavaInstant()))
        }
    }

    @Test
    fun `map Java Instant to Kotlin Instant implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.InstantMappersTest.*

                class Mapper : ObjectMappie<JInstantWrapper, InstantWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = JInstant.EPOCH

            val mapper = objectMappie<JInstantWrapper, InstantWrapper>()

            assertThat(mapper.map(JInstantWrapper(input)))
                .isEqualTo(InstantWrapper(input.toKotlinInstant()))
        }
    }
}