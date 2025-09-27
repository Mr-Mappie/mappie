package tech.mappie.api.kotlinx.datetime

import kotlinx.datetime.FixedOffsetTimeZone
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toJavaZoneId
import kotlinx.datetime.toJavaZoneOffset
import kotlinx.datetime.toKotlinFixedOffsetTimeZone
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.datetime.toKotlinUtcOffset
import tech.mappie.api.ObjectMappie
import java.time.ZoneId
import java.time.ZoneOffset

public object KotlinTimeZoneToJavaZoneIdMapper : ObjectMappie<TimeZone, ZoneId>() {
    override fun map(from: TimeZone): ZoneId =
        from.toJavaZoneId()
}

public object JavaZoneIdToKotlinTimeZoneMapper : ObjectMappie<ZoneId, TimeZone>() {
    override fun map(from: ZoneId): TimeZone =
        from.toKotlinTimeZone()
}

public object KotlinUtcOffsetToJavaZoneOffsetMapper : ObjectMappie<UtcOffset, ZoneOffset>() {
    override fun map(from: UtcOffset): ZoneOffset =
        from.toJavaZoneOffset()
}

public object JavaZoneOffsetToKotlinUtcOffsetMapper : ObjectMappie<ZoneOffset, UtcOffset>() {
    override fun map(from: ZoneOffset): UtcOffset =
        from.toKotlinUtcOffset()
}

//
public object JavaZoneOffsetToKotlinTimeZoneMapper : ObjectMappie<ZoneOffset, TimeZone>() {
    override fun map(from: ZoneOffset): TimeZone =
        from.toKotlinTimeZone()
}

public object JavaZoneOffsetToKotlinFixedOffsetTimeZoneMapper : ObjectMappie<ZoneOffset, FixedOffsetTimeZone>() {
    override fun map(from: ZoneOffset): FixedOffsetTimeZone =
        from.toKotlinFixedOffsetTimeZone()
}

public object KotlinFixedOffsetTimeZoneToJavaZoneOffsetMapper : ObjectMappie<FixedOffsetTimeZone, ZoneOffset>() {
    override fun map(from: FixedOffsetTimeZone): ZoneOffset =
        from.toJavaZoneOffset()
}
