package tech.mappie.api.kotlinx.datetime

import java.time.Month as JMonth
import kotlinx.datetime.Month
import kotlinx.datetime.toJavaMonth
import kotlinx.datetime.toKotlinMonth
import tech.mappie.api.EnumMappie

public object KotlinMonthToJavaMonthMapper : EnumMappie<Month, JMonth>() {
    override fun map(from: Month): JMonth =
        from.toJavaMonth()
}

public object JavaMonthToKotlinMonthMapper : EnumMappie<JMonth, Month>() {
    override fun map(from: JMonth): Month =
        from.toKotlinMonth()
}
