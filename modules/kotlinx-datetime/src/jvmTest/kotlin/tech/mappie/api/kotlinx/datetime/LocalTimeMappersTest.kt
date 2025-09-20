package tech.mappie.api.kotlinx.datetime

import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toKotlinLocalTime
import java.time.LocalTime as JLocalTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class LocalTimeMappersTest : MappieTestCase() {

    data class LocalTimeWrapper(val value: LocalTime)

    data class JLocalTimeWrapper(val value: JLocalTime)

    @Test
    fun `map Kotlin LocalTime to Java LocalTime implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.LocalTimeMappersTest.*

                class Mapper : ObjectMappie<LocalTimeWrapper, JLocalTimeWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = LocalTime(10, 1, 2, 3)

            val mapper = objectMappie<LocalTimeWrapper, JLocalTimeWrapper>()

            assertThat(mapper.map(LocalTimeWrapper(input)))
                .isEqualTo(JLocalTimeWrapper(input.toJavaLocalTime()))
        }
    }

    @Test
    fun `map Java LocalTime to Kotlin LocalTime implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.LocalTimeMappersTest.*

                class Mapper : ObjectMappie<JLocalTimeWrapper, LocalTimeWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = JLocalTime.MIDNIGHT

            val mapper = objectMappie<JLocalTimeWrapper, LocalTimeWrapper>()

            assertThat(mapper.map(JLocalTimeWrapper(input)))
                .isEqualTo(LocalTimeWrapper(input.toKotlinLocalTime()))
        }
    }
}