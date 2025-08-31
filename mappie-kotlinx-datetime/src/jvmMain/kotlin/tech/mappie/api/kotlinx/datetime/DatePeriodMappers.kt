package tech.mappie.api.kotlinx.datetime

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.toJavaPeriod
import kotlinx.datetime.toKotlinDatePeriod
import tech.mappie.api.ObjectMappie
import java.time.Period

public object KotlinDatePeriodToJavaPeriodMapper : ObjectMappie<DatePeriod, Period>() {
    override fun map(from: DatePeriod): Period =
        from.toJavaPeriod()
}

public object JavaPeriodToKotlinDatePeriodMapper : ObjectMappie<Period, DatePeriod>() {
    override fun map(from: Period): DatePeriod =
        from.toKotlinDatePeriod()
}
