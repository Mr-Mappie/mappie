package tech.mappie.api.kotlinx.datetime

import java.time.Instant as JInstant
import tech.mappie.api.ObjectMappie
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@OptIn(ExperimentalTime::class)
public object KotlinInstantToJavaInstantMapper : ObjectMappie<Instant, JInstant>() {
    override fun map(from: Instant): JInstant =
        from.toJavaInstant()
}

@OptIn(ExperimentalTime::class)
public object JavaInstantToKotlinInstantMapper : ObjectMappie<JInstant, Instant>() {
    override fun map(from: JInstant): Instant =
        from.toKotlinInstant()
}