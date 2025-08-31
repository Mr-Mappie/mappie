package tech.mappie.api.kotlinx.datetime

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate as JLocalDate
import tech.mappie.api.ObjectMappie

public object KotlinLocalDateToJavaLocalDateMapper : ObjectMappie<LocalDate, JLocalDate>() {
    override fun map(from: LocalDate): JLocalDate =
        from.toJavaLocalDate()
}

public object JavaLocalDateToKotlinLocalDateMapper : ObjectMappie<JLocalDate, LocalDate>() {
    override fun map(from: JLocalDate): LocalDate =
        from.toKotlinLocalDate()
}
