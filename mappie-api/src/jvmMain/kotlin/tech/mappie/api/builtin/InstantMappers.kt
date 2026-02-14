package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import java.time.Instant as JInstant

public class JavaInstantToKotlinInstantMapper : ObjectMappie<JInstant, Instant>() {
    override fun map(from: JInstant): Instant = from.toKotlinInstant()
}

public class KotlinInstantToJavaInstantMapper : ObjectMappie<Instant, JInstant>() {
    override fun map(from: Instant): JInstant = from.toJavaInstant()
}
