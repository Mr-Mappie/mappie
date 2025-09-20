package tech.mappie.api.kotlinx.datetime

import kotlinx.datetime.FixedOffsetTimeZone
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.toJavaZoneId
import kotlinx.datetime.toJavaZoneOffset
import kotlinx.datetime.toKotlinFixedOffsetTimeZone
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.datetime.toKotlinUtcOffset
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.time.ZoneId
import java.time.ZoneOffset

class ZoneMappersTest : MappieTestCase() {

    data class TimeZoneWrapper(val value: TimeZone)
    data class ZoneIdWrapper(val value: ZoneId)
    data class UtcOffsetWrapper(val value: UtcOffset)
    data class ZoneOffsetWrapper(val value: ZoneOffset)
    data class FixedOffsetTimeZoneWrapper(val value: FixedOffsetTimeZone)

    @Test
    fun `map TimeZone to ZoneId implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.ZoneMappersTest.*

                class Mapper : ObjectMappie<TimeZoneWrapper, ZoneIdWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = TimeZone.currentSystemDefault()

            val mapper = objectMappie<TimeZoneWrapper, ZoneIdWrapper>()

            assertThat(mapper.map(TimeZoneWrapper(input)))
                .isEqualTo(ZoneIdWrapper(input.toJavaZoneId()))
        }
    }

    @Test
    fun `map ZoneId to TimeZone implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.ZoneMappersTest.*

                class Mapper : ObjectMappie<ZoneIdWrapper, TimeZoneWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = ZoneId.systemDefault()

            val mapper = objectMappie<ZoneIdWrapper, TimeZoneWrapper>()

            assertThat(mapper.map(ZoneIdWrapper(input)))
                .isEqualTo(TimeZoneWrapper(input.toKotlinTimeZone()))
        }
    }

    @Test
    fun `map UtcOffset to ZoneOffset implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.ZoneMappersTest.*

                class Mapper : ObjectMappie<UtcOffsetWrapper, ZoneOffsetWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = UtcOffset.ZERO

            val mapper = objectMappie<UtcOffsetWrapper, ZoneOffsetWrapper>()

            assertThat(mapper.map(UtcOffsetWrapper(input)))
                .isEqualTo(ZoneOffsetWrapper(input.toJavaZoneOffset()))
        }
    }

    @Test
    fun `map ZoneOffset to UtcOffset implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.ZoneMappersTest.*

                class Mapper : ObjectMappie<ZoneOffsetWrapper, UtcOffsetWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = ZoneOffset.UTC

            val mapper = objectMappie<ZoneOffsetWrapper, UtcOffsetWrapper>()

            assertThat(mapper.map(ZoneOffsetWrapper(input)))
                .isEqualTo(UtcOffsetWrapper(input.toKotlinUtcOffset()))
        }
    }

    @Test
    fun `map ZoneOffset to TimeZone implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.ZoneMappersTest.*

                class Mapper : ObjectMappie<ZoneOffsetWrapper, TimeZoneWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = ZoneOffset.UTC

            val mapper = objectMappie<ZoneOffsetWrapper, TimeZoneWrapper>()

            assertThat(mapper.map(ZoneOffsetWrapper(input)))
                .isEqualTo(TimeZoneWrapper(input.toKotlinTimeZone()))
        }
    }

    @Test
    fun `map ZoneOffset to FixedOffsetTimeZone implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.ZoneMappersTest.*

                class Mapper : ObjectMappie<ZoneOffsetWrapper, FixedOffsetTimeZoneWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = ZoneOffset.UTC

            val mapper = objectMappie<ZoneOffsetWrapper, FixedOffsetTimeZoneWrapper>()

            assertThat(mapper.map(ZoneOffsetWrapper(input)))
                .isEqualTo(FixedOffsetTimeZoneWrapper(input.toKotlinFixedOffsetTimeZone()))
        }
    }

    @Test
    fun `map FixedOffsetTimeZone to ZoneOffset implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.datetime.ZoneMappersTest.*

                class Mapper : ObjectMappie<FixedOffsetTimeZoneWrapper, ZoneOffsetWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = UtcOffset.ZERO.asTimeZone()

            val mapper = objectMappie<FixedOffsetTimeZoneWrapper, ZoneOffsetWrapper>()

            assertThat(mapper.map(FixedOffsetTimeZoneWrapper(input)))
                .isEqualTo(ZoneOffsetWrapper(input.toJavaZoneOffset()))
        }
    }
}