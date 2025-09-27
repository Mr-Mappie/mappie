package tech.mappie.api.builtin

import tech.mappie.api.PredefinedMappieProvider

public class BuiltInMappieProvider : PredefinedMappieProvider {
    public override val common: List<String> = listOf(
        "tech/mappie/api/builtin/ByteToShortMapper",
        "tech/mappie/api/builtin/ByteToIntMapper",
        "tech/mappie/api/builtin/ByteToLongMapper",
        "tech/mappie/api/builtin/ByteToStringMapper",
        "tech/mappie/api/builtin/CharToStringMapper",
        "tech/mappie/api/builtin/DoubleToStringMapper",
        "tech/mappie/api/builtin/FloatToDoubleMapper",
        "tech/mappie/api/builtin/FloatToStringMapper",
        "tech/mappie/api/builtin/IntToLongMapper",
        "tech/mappie/api/builtin/IntToStringMapper",
        "tech/mappie/api/builtin/LongToStringMapper",
        "tech/mappie/api/builtin/ShortToIntMapper",
        "tech/mappie/api/builtin/ShortToLongMapper",
        "tech/mappie/api/builtin/ShortToStringMapper",
    )

    public override val jvm: List<String> = listOf(
        "tech/mappie/api/builtin/LocalDateTimeToLocalTimeMapper",
        "tech/mappie/api/builtin/LocalDateTimeToLocalDateMapper",
        "tech/mappie/api/builtin/ByteToBigIntegerMapper",
        "tech/mappie/api/builtin/ByteToBigDecimalMapper",
        "tech/mappie/api/builtin/ShortToBigIntegerMapper",
        "tech/mappie/api/builtin/ShortToBigDecimalMapper",
        "tech/mappie/api/builtin/IntToBigIntegerMapper",
        "tech/mappie/api/builtin/IntToBigDecimalMapper",
        "tech/mappie/api/builtin/LongToBigIntegerMapper",
        "tech/mappie/api/builtin/LongToBigDecimalMapper",
        "tech/mappie/api/builtin/FloatToBigDecimalMapper",
        "tech/mappie/api/builtin/BigIntegerToStringMapper",
        "tech/mappie/api/builtin/BigDecimalToStringMapper",
        "tech/mappie/api/builtin/UUIDToStringMapper",
        "tech/mappie/api/builtin/DoubleToBigDecimalMapper",
    )
}