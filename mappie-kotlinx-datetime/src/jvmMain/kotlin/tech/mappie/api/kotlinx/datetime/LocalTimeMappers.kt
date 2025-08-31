package tech.mappie.api.kotlinx.datetime

import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toKotlinLocalTime
import java.time.LocalTime as JLocalTime
import tech.mappie.api.ObjectMappie

public object KotlinLocalTimeToJavaLocalTimeMapper : ObjectMappie<LocalTime, JLocalTime>() {
    override fun map(from: LocalTime): JLocalTime =
        from.toJavaLocalTime()
}

public object JavaLocalTimeToKotlinLocalTimeMapper : ObjectMappie<JLocalTime, LocalTime>() {
    override fun map(from: JLocalTime): LocalTime =
        from.toKotlinLocalTime()
}