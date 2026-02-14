package tech.mappie.api.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import java.time.Instant as JInstant

class InstantMappersTest : MappieTestCase() {

    data class JavaInstant(val value: JInstant)

    data class KotlinInstant(val value: Instant)

    @Test
    fun `map Kotlin Instant to Java Instant implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.InstantMappersTest.*

                class Mapper : ObjectMappie<KotlinInstant, JavaInstant>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<KotlinInstant, JavaInstant>()

            assertThat(mapper.map(KotlinInstant(Instant.DISTANT_PAST)))
                .isEqualTo(JavaInstant(Instant.DISTANT_PAST.toJavaInstant()))
        }
    }

    @Test
    fun `map Kotlin Instant to Java Instant explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.api.builtin.InstantMappersTest.*

                        class Mapper : ObjectMappie<KotlinInstant, JavaInstant>() {
                            override fun map(from: KotlinInstant) = mapping {
                                to::value fromProperty from::value via KotlinInstantToJavaInstantMapper()
                            }
                        }
                        """
            )
        } satisfies {
           isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<KotlinInstant, JavaInstant>()

            assertThat(mapper.map(KotlinInstant(Instant.DISTANT_FUTURE)))
                .isEqualTo(JavaInstant(Instant.DISTANT_FUTURE.toJavaInstant()))
        }
    }

    @Test
    fun `map Java Instant to Kotlin Instant implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.InstantMappersTest.*

                class Mapper : ObjectMappie<JavaInstant, KotlinInstant>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<JavaInstant, KotlinInstant>()

            assertThat(mapper.map(JavaInstant(JInstant.MIN)))
                .isEqualTo(KotlinInstant(JInstant.MIN.toKotlinInstant()))
        }
    }

    @Test
    fun `map Java Instant to Kotlin Instant explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.api.builtin.InstantMappersTest.*

                        class Mapper : ObjectMappie<JavaInstant, KotlinInstant>() {
                            override fun map(from: JavaInstant) = mapping {
                                to::value fromProperty from::value via JavaInstantToKotlinInstantMapper()
                            }
                        }
                        """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<JavaInstant, KotlinInstant>()

            assertThat(mapper.map(JavaInstant(JInstant.EPOCH)))
                .isEqualTo(KotlinInstant(JInstant.EPOCH.toKotlinInstant()))
        }
    }
}