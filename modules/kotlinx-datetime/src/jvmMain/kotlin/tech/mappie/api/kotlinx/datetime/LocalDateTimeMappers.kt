package tech.mappie.api.kotlinx.datetime

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime as JLocalDateTime
import tech.mappie.api.ObjectMappie

public object KotlinLocalDateTimeToJavaLocalDateTimeMapper : ObjectMappie<LocalDateTime, JLocalDateTime>() {
    override fun map(from: LocalDateTime): JLocalDateTime =
        from.toJavaLocalDateTime()
}

public object JavaLocalDateTimeToKotlinLocalDateTimeMapper : ObjectMappie<JLocalDateTime, LocalDateTime>() {
    override fun map(from: JLocalDateTime): LocalDateTime =
        from.toKotlinLocalDateTime()
}