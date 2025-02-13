package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie

public class ByteToShortMapper : ObjectMappie<Byte, Short>() {
    override fun map(from: Byte): Short =
        from.toShort()
}

public class ByteToIntMapper : ObjectMappie<Byte, Int>() {
    override fun map(from: Byte): Int =
        from.toInt()
}

public class ByteToLongMapper : ObjectMappie<Byte, Long>() {
    override fun map(from: Byte): Long =
        from.toLong()
}

public class ByteToStringMapper : ObjectMappie<Byte, String>() {
    override fun map(from: Byte): String =
        from.toString()
}
