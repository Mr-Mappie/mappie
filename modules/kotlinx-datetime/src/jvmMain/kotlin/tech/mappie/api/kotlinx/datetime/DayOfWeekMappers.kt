package tech.mappie.api.kotlinx.datetime

import java.time.DayOfWeek as JDayOfWeek
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toJavaDayOfWeek
import kotlinx.datetime.toKotlinDayOfWeek
import tech.mappie.api.EnumMappie

public object KotlinDayOfWeekToJavaDayOfWeekMapper : EnumMappie<DayOfWeek, JDayOfWeek>() {
    override fun map(from: DayOfWeek): JDayOfWeek =
        from.toJavaDayOfWeek()
}

public object JavaDayOfWeekToKotlinDayOfWeekMapper : EnumMappie<JDayOfWeek, DayOfWeek>() {
    override fun map(from: JDayOfWeek): DayOfWeek =
        from.toKotlinDayOfWeek()
}
