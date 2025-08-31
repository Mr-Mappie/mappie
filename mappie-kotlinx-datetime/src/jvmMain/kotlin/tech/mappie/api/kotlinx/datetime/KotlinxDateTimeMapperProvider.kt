package tech.mappie.api.kotlinx.datetime

import tech.mappie.api.PredefinedMapperProvider

internal class KotlinxDateTimeMapperProvider : PredefinedMapperProvider() {
    override val common = emptyList<String>()

    override val jvm = listOf(
        "tech/mappie/api/kotlinx/datetime/KotlinDatePeriodToJavaPeriodMapper",
        "tech/mappie/api/kotlinx/datetime/JavaPeriodToKotlinDatePeriodMapper",
        "tech/mappie/api/kotlinx/datetime/KotlinDayOfWeekToJavaDayOfWeekMapper",
        "tech/mappie/api/kotlinx/datetime/JavaDayOfWeekToKotlinDayOfWeekMapper",
        "tech/mappie/api/kotlinx/datetime/KotlinInstantToJavaInstantMapper",
        "tech/mappie/api/kotlinx/datetime/JavaInstantToKotlinInstantMapper",
        "tech/mappie/api/kotlinx/datetime/KotlinLocalDateToJavaLocalDateMapper",
        "tech/mappie/api/kotlinx/datetime/JavaLocalDateToKotlinLocalDateMapper",
        "tech/mappie/api/kotlinx/datetime/KotlinLocalDateTimeToJavaLocalDateTimeMapper",
        "tech/mappie/api/kotlinx/datetime/JavaLocalDateTimeToKotlinLocalDateTimeMapper",
        "tech/mappie/api/kotlinx/datetime/KotlinLocalTimeToJavaLocalTimeMapper",
        "tech/mappie/api/kotlinx/datetime/JavaLocalTimeToKotlinLocalTimeMapper",
        "tech/mappie/api/kotlinx/datetime/KotlinMonthToJavaMonthMapper",
        "tech/mappie/api/kotlinx/datetime/JavaMonthToKotlinMonthMapper",
        "tech/mappie/api/kotlinx/datetime/KotlinTimeZoneToJavaZoneIdMapper",
        "tech/mappie/api/kotlinx/datetime/JavaZoneIdToKotlinTimeZoneMapper",
        "tech/mappie/api/kotlinx/datetime/KotlinUtcOffsetToJavaZoneOffsetMapper",
        "tech/mappie/api/kotlinx/datetime/JavaZoneOffsetToKotlinUtcOffsetMapper",
        "tech/mappie/api/kotlinx/datetime/JavaZoneOffsetToKotlinTimeZoneMapper",
        "tech/mappie/api/kotlinx/datetime/JavaZoneOffsetToKotlinFixedOffsetTimeZoneMapper",
        "tech/mappie/api/kotlinx/datetime/KotlinFixedOffsetTimeZoneToJavaZoneOffsetMapper",
    )
}